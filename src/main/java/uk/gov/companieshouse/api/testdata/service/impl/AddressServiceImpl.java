package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.AddressService;

@Service
public class AddressServiceImpl implements AddressService {

    private static final Address ENGLAND_WALES_ADDRESS = new Address(
            "Companies House",
            "Crownway",
            "United Kingdom",
            "Cardiff",
            "CF14 3UZ"
    );
    private static final Address SCOTLAND_ADDRESS = new Address(
            "4th Floor Edinburgh Quay 2",
            "139 Fountain Bridge",
            "United Kingdom",
            "Edinburgh",
            "EH3 9FF"
    );

    public Address getAddress(Jurisdiction jurisdiction) {
        switch (jurisdiction) {
            case ENGLAND_WALES:
                return ENGLAND_WALES_ADDRESS;
            case SCOTLAND:
                return SCOTLAND_ADDRESS;
            default:
                throw new IllegalArgumentException("No address for jurisdiction");
        }
    }
}
