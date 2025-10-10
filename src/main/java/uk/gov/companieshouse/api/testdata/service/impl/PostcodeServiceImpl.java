package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodeRepository;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class PostcodeServiceImpl implements PostcodeService {

    @Autowired
    private PostcodeRepository postcodeRepository;

    @Autowired
    private RandomService randomService;

    @Override
    public List<Postcodes> get(String country) {
        final List<String> prefixes = getPrefixes(country);

        if (prefixes.size() == 1) {
            return queryByPrefix(prefixes.getFirst());
        }

        int size = prefixes.size();
        boolean[] tried = new boolean[size];
        int triedCount = 0;

        while (triedCount < size) {
            int idx = (int) randomService.getNumberInRange(0, size - 1).orElse(0);

            if (tried[idx]) {
                int next = idx;
                do { next = (next + 1) % size; } while (tried[next] && next != idx);
                idx = next;
            }

            if (tried[idx]) break; // safety (shouldn’t happen)
            tried[idx] = true; triedCount++;

            List<Postcodes> result = queryByPrefix(prefixes.get(idx));
            if (!result.isEmpty()) return result;
        }

        return List.of();
    }

    private static List<String> getPrefixes(String country) {
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

    private List<Postcodes> queryByPrefix(String prefix) {
        Map<String, Object> regex = new HashMap<>();
        regex.put("$regex", "^" + prefix);

        Map<String, Object> condition = new HashMap<>();
        condition.put("postcode.stripped", regex);

        var pageRequest = PageRequest.of(0, 10);
        List<Postcodes> result = postcodeRepository.findByPostcodePrefixContaining(List.of(condition), pageRequest);
        return result == null ? List.of() : result;
    }
}
