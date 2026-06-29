package uk.gov.companieshouse.api.testdata.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.PostcodeLoadException;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class PostcodeServiceImpl implements PostcodeService {

    @Autowired
    private RandomService randomService;

    private static final String FAILED_TO_READ_POSTCODES = "Failed to read postcodes.json";
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Override
    public PostcodesResponse getPostcodes(String country) throws DataException {
        try {
            List<Postcodes> postcodes = getPostcodeByCountry(country);
            if (postcodes == null || postcodes.isEmpty()) {
                LOG.info("No postcodes found for country: " + country);
                return null;
            }
            var secureRandom = new SecureRandom();
            var randomPostcode = secureRandom.nextInt(postcodes.size());
            return getPostCodesData(postcodes).get(randomPostcode);
        } catch (Exception ex) {
            throw new DataException("Error retrieving postcodes", ex);
        }
    }

    private static List<PostcodesResponse> getPostCodesData(List<Postcodes> postcodes) {
        List<PostcodesResponse> postcodesResponseList = new ArrayList<>();
        for (Postcodes postcode : postcodes) {
            var postcodeData = new PostcodesResponse(
                    postcode.getBuildingNumber() != null ? postcode
                            .getBuildingNumber().intValue() : null,
                    postcode.getThoroughfare().getName() + " "
                            + (postcode.getThoroughfare().getDescriptor()
                            != null ? postcode.getThoroughfare().getDescriptor() : ""),
                    postcode.getLocality().getDependentLocality(),
                    postcode.getLocality().getPostTown(),
                    postcode.getPostcode().getPretty()
            );
            postcodesResponseList.add(postcodeData);
        }
        return postcodesResponseList;
    }

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
        List<String> prefixesScotland = List.of("AB", "DD", "DG", "EH", "FK", "G1", "G2", "G3", "G4", "G5", "G6", "G7", "G8", "HS", "IV", "KA", "KW", "KY", "ML", "PA", "PH", "TD");
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
            case "gb-wls", "wls" -> prefixesWales;
            case "gb-sct", "sct" -> prefixesScotland;
            case "gb-nir", "nir" -> prefixesNI;
            case "gb-eng", "eng" -> prefixesEngland;
            default -> List.of("INVALID");
        };
    }

    private List<Postcodes> queryByPrefix(String prefix) {
        List<Postcodes> allPostcodes = loadAllPostcodes();
        if (allPostcodes.isEmpty()) {
            LOG.error("No postcodes loaded from postcodes.json");
            return List.of();
        }

        return allPostcodes.stream()
                .filter(p -> p.getPostcode() != null && p.getPostcode().getStripped().startsWith(prefix))
                .filter(p -> p.getBuildingNumber() != null)
                .limit(10)
                .toList();
    }

    List<Postcodes> loadAllPostcodes() {
        try (var inputStream = getPostcodesResourceStream()) {
            if (inputStream == null) {
                LOG.error("postcodes.json not found in resources");
                return List.of();
            }
            var mapper = new ObjectMapper();
            return mapper.readValue(inputStream, new TypeReference<List<Postcodes>>() {});
        } catch (IOException ioException) {
            LOG.error(FAILED_TO_READ_POSTCODES, ioException);
            throw new PostcodeLoadException(FAILED_TO_READ_POSTCODES, ioException);
        } catch (JacksonException jacksonException) {
            LOG.error(FAILED_TO_READ_POSTCODES, jacksonException);
            throw new PostcodeLoadException(FAILED_TO_READ_POSTCODES, jacksonException);
        }
    }

    protected InputStream getPostcodesResourceStream() {
        return getClass().getClassLoader().getResourceAsStream("postcodes.json");
    }
}
