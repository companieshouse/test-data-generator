package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private static final String UNITED_KINGDOM = "United Kingdom";
    private static final Address ENGLAND_WALES_ADDRESS = new Address(
            "1",
            "Companies House",
            "Crownway",
            UNITED_KINGDOM,
            "Cardiff",
            "CF14 3UZ"
    );
    private static final Address SCOTLAND_ADDRESS = new Address(
            "1",
            "4th Floor Edinburgh Quay 2",
            "139 Fountain Bridge",
            UNITED_KINGDOM,
            "Edinburgh",
            "EH3 9FF"
    );
    private static final Address NI_ADDRESS = new Address(
            "1",
            "Second Floor The Linenhall",
            "32 - 38 Linenhall Street",
            UNITED_KINGDOM,
            "Belfast",
            "BT2 8BG"
    );

    @Override
    public Address getAddress(Jurisdiction jurisdiction) {
        switch (jurisdiction) {
            case ENGLAND_WALES:
                return ENGLAND_WALES_ADDRESS;
            case SCOTLAND:
                return SCOTLAND_ADDRESS;
            case NI:
                return NI_ADDRESS;
            default:
                throw new IllegalArgumentException("No address for jurisdiction");
        }
    }

    @Override
    public String getCountryOfResidence(Jurisdiction jurisdiction) {
        switch(jurisdiction) {
            case ENGLAND_WALES:
                return "Wales";
            case SCOTLAND:
                return "Scotland";
            case NI:
                return "Northern Ireland";
            default:
                throw new IllegalArgumentException("No valid jurisdiction provided");
        }
    }
}
