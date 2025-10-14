package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodeRepository;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class PostcodeServiceImpl implements PostcodeService {

    @Autowired
    private PostcodeRepository postcodeRepository;

    @Autowired
    private RandomService randomService;

    private final Map<String, List<Postcodes>> cache = new HashMap<>();

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Override
    public List<Postcodes> getPostcodeByCountry(String country) {
        final List<String> postcodePrefixes = getPostcodePrefixes(country);

        var size = postcodePrefixes.size();
        if (size == 1) {
            return queryByPrefix(postcodePrefixes.getFirst());
        }

        var tried = new boolean[size];
        var triedCount = 0;

        // Try random postcode prefixes until we find one that returns results, or we run out of postcode prefixes
        while (triedCount < size) {
            var postcodePrefixIndex = (int) randomService.getNumberInRange(0, size - 1).orElse(0);

            // Skip already-tried postcodePrefixes
            if (tried[postcodePrefixIndex]) {
                LOG.debug("Prefix at index " + postcodePrefixIndex + " already tried, finding next postcode prefix index.");
                int nextPostcodePrefixIndex = postcodePrefixIndex;
                do {
                    nextPostcodePrefixIndex = (nextPostcodePrefixIndex + 1) % size;
                } while (tried[nextPostcodePrefixIndex] && nextPostcodePrefixIndex != postcodePrefixIndex);
                LOG.debug("Next index to try is " + nextPostcodePrefixIndex);
                postcodePrefixIndex = nextPostcodePrefixIndex;
            }

            // Safety check - if all tried, break
            if (tried[postcodePrefixIndex]) break;
            tried[postcodePrefixIndex] = true;
            triedCount++;

            List<Postcodes> result = queryByPrefix(postcodePrefixes.get(postcodePrefixIndex));
            if (!result.isEmpty()) {
                LOG.info(String.format("Tried prefix %s got %s results", postcodePrefixes.get(postcodePrefixIndex), result));
                return result;
            }
        }

        return List.of();
    }

    private static List<String> getPostcodePrefixes(String country) {
        List<String> prefixesWales = List.of("CF", "LL", "NP", "LD", "SA");
        List<String> prefixesScotland = List.of("AB", "DD", "DG", "EH", "FK", "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "G9", "HS", "IV", "KA", "KW", "KY", "ML", "PA", "PH", "TD");
        List<String> prefixesEngland = List.of(
                "AL", "B", "BA", "BB", "BD", "BH", "BL", "BN", "BR", "BS",
                "CB", "CM", "CO", "CR", "CT", "CV", "DA", "DE", "DH", "DL", "DN", "DT", "DY",
                "E", "EC", "EN", "EX", "HA", "HD", "HG", "HP", "HU", "IG", "IP",
                "L", "LA", "LE", "LN", "LS", "LU",
                "M", "ME", "MK", "N", "NE", "NG", "NN", "NR", "NW",
                "OL", "OX",
                "PE", "PL", "PO", "PR",
                "RG", "RH", "RM",
                "S", "SE", "SG", "SK", "SL", "SM", "SN", "SO", "SP", "SR", "SS", "ST", "SW",
                "TA", "TF", "TN", "TQ", "TR", "TS", "TW",
                "UB",
                "W", "WA", "WC", "WD", "WF", "WN", "WR", "WS", "WV",
                "YO"
        );
        List<String> prefixesNI = List.of("BT");

        return switch (country.toLowerCase()) {
            case "gb-wls" -> prefixesWales;
            case "gb-sct" -> prefixesScotland;
            case "gb-nir" -> prefixesNI;
            case "gb-eng" -> prefixesEngland;
            default -> throw new IllegalArgumentException("Country not recognised: " + country);
        };
    }

    @Cacheable("postcodesByPrefix")
    private List<Postcodes> queryByPrefix(String prefix) {
        if (cache.containsKey(prefix)) {
            return cache.get(prefix);
        }

        Map<String, Object> regex = new HashMap<>();
        regex.put("$regex", "^" + prefix);

        Map<String, Object> condition = new HashMap<>();
        condition.put("postcode.stripped", regex);
        Map<String, Object> buildingNumberConditionMap = new HashMap<>();
        buildingNumberConditionMap.put("$ne", null);
        condition.put("building_number", buildingNumberConditionMap);

        var pageRequest = PageRequest.of(0, 10);
        List<Postcodes> result = postcodeRepository.findByPostcodePrefixContaining(List.of(condition), pageRequest);

        // Ensure we never return null - always return empty list if no results
        result = result == null ? List.of() : result;
        cache.put(prefix, result);
        return result;
    }
}
