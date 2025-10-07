package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodesRepository;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;

@Service
public class PostcodeServiceImpl implements PostcodeService {

    @Autowired
    private PostcodesRepository postcodesRepository;

    @Override
    public List<Postcodes> get(String country) {
        // Prefix lists (trimmed for brevity — keep your full lists)
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

        List<String> prefixes = switch (country.toLowerCase()) {
            case "gb-wls", "gb-cym" -> prefixesWales;
            case "gb-sct" -> prefixesScotland;
            case "gb-nir" -> prefixesNI;
            case "gb-eng" -> prefixesEngland;
            default -> throw new IllegalArgumentException("Country not recognised: " + country);
        };

        List<Object> orConditions = prefixes.stream().map(prefix -> {
            Map<String, Object> regex = new HashMap<>();
            regex.put("$regex", "^" + prefix);
            Map<String, Object> condition = new HashMap<>();
            condition.put("postcode.stripped", regex);
            return condition;
        }).collect(Collectors.toList());

        return postcodesRepository.findByStrippedContaining(orConditions, PageRequest.of(0, 10));
    }
}
