package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressServiceImplTest {

    private AddressServiceImpl addressService;

    @BeforeEach
    void init() {
        this.addressService = new AddressServiceImpl();
    }

    @Test
    void getAddressForJurisdictionEnglandWales() {
        Address address = addressService.getAddress(Jurisdiction.ENGLAND_WALES);
        assertEquals("Companies House", address.getAddressLine1());
        assertEquals("Crownway", address.getAddressLine2());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("Cardiff", address.getLocality());
        assertEquals("CF14 3UZ", address.getPostalCode());
    }

    @Test
    void getAddressForJurisdictionScotland() {
        Address address = addressService.getAddress(Jurisdiction.SCOTLAND);
        assertEquals("4th Floor Edinburgh Quay 2", address.getAddressLine1());
        assertEquals("139 Fountain Bridge", address.getAddressLine2());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("Edinburgh", address.getLocality());
        assertEquals("EH3 9FF", address.getPostalCode());
    }

    @Test
    void getAddressForJurisdictionNI() {
        Address address = addressService.getAddress(Jurisdiction.NI);
        assertEquals("Second Floor The Linenhall", address.getAddressLine1());
        assertEquals("32 - 38 Linenhall Street", address.getAddressLine2());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("Belfast", address.getLocality());
        assertEquals("BT2 8BG", address.getPostalCode());
    }

    @Test
    void getAddressForJurisdictionUnitedKingdom() {
        Address address = addressService.getAddress(Jurisdiction.UNITED_KINGDOM);
        assertEquals("Gordon Cummins Hwy", address.getAddressLine1());
        assertEquals("Grantley Adams International Airport", address.getAddressLine2());
        assertEquals("Barbados", address.getCountry());
        assertEquals("Christ Church", address.getLocality());
        assertEquals("123123", address.getPostalCode());
    }

    @Test
    void getCountryOfResidenceEnglandWales() {
        String s = addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES);
        assertEquals("Wales", s);
    }

    @Test
    void getCountryOfResidenceScotland() {
        String s = addressService.getCountryOfResidence(Jurisdiction.SCOTLAND);
        assertEquals("Scotland", s);
    }

    @Test
    void getCountryOfResidenceNI() {
        String s = addressService.getCountryOfResidence(Jurisdiction.NI);
        assertEquals("Northern Ireland", s);
    }

    @Test
    void getCountryOfResidenceUnitedKingdom() {
        String s = addressService.getCountryOfResidence(Jurisdiction.UNITED_KINGDOM);
        assertEquals("Barbados", s);
    }
}