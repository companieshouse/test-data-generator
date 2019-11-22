package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AddressServiceImplTest {

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

    @InjectMocks
    private AddressServiceImpl addressService;

    @Test
    void getAddressForJurisdictionEngland() {
        Address address = addressService.getAddressForJurisdiction(Jurisdiction.ENGLAND_WALES);
        assertEquals(ENGLAND_WALES_ADDRESS, address);
    }

    @Test
    void getAddressForJurisdictionScotland() {
        Address address = addressService.getAddressForJurisdiction(Jurisdiction.SCOTLAND);
        assertEquals(SCOTLAND_ADDRESS, address);
    }
}