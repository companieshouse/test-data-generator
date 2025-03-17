package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;


class AddressServiceImplTest {

    private AddressServiceImpl addressService;

    @BeforeEach
    void init() {
        this.addressService = new AddressServiceImpl();
    }

    @Test
    void getAddressForJurisdictionEnglandWales() {
        Address address = addressService.getAddress(Jurisdiction.ENGLAND_WALES);
        assertAddress(address, "Companies House", "Crownway",
                "United Kingdom", "Cardiff", "CF14 3UZ");
    }

    @Test
    void getAddressForJurisdictionScotland() {
        Address address = addressService.getAddress(Jurisdiction.SCOTLAND);
        assertAddress(address, "4th Floor Edinburgh Quay 2", "139 Fountain Bridge",
                "United Kingdom", "Edinburgh", "EH3 9FF");
    }

    @Test
    void getAddressForJurisdictionNI() {
        Address address = addressService.getAddress(Jurisdiction.NI);
        assertAddress(address, "Second Floor The Linenhall", "32 - 38 Linenhall Street",
                "United Kingdom", "Belfast", "BT2 8BG");
    }

    @Test
    void getAddressForJurisdictionUnitedKingdom() {
        Address address = addressService.getAddress(Jurisdiction.UNITED_KINGDOM);
        assertAddress(address, "Gordon Cummins Hwy", "Grantley Adams International Airport",
                "Barbados", "Christ Church", "123125");
    }

    @Test
    void getCountryOfResidenceEnglandWales() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.ENGLAND_WALES);
        assertEquals("Wales", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceScotland() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.SCOTLAND);
        assertEquals("Scotland", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceNI() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.NI);
        assertEquals("Northern Ireland", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceUnitedKingdom() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.UNITED_KINGDOM);
        assertEquals("Barbados", addressServiceCountryOfResidence);
    }

    @Test
    void getAddressForJurisdictionEngland() {
        Address address = addressService.getAddress(Jurisdiction.ENGLAND);
        assertAddress(address, "Companies House,4th Floor, The Linen Hall",
                "162-168 Regent Street", "United Kingdom", "London", "W1B 5TF");
    }

    @Test
    void getAddressForJurisdictionEuropeanUnion() {
        Address address = addressService.getAddress(Jurisdiction.EUROPEAN_UNION);
        assertAddress(address, "Schiphol Boulevard Tower 403 Tower C-4",
                "1118bk Schiphol", "Netherlands", "Amsterdam", "123123");
    }

    @Test
    void getAddressForJurisdictionNonEu() {
        Address address = addressService.getAddress(Jurisdiction.NON_EU);
        assertAddress(address, "Edificio Salduba Tercer Piso", "Calle 53 Este",
                "Panama", "Marbella", "123124");
    }

    @Test
    void getCountryOfResidenceEngland() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.ENGLAND);
        assertEquals("England", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceEuropeanUnion() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.EUROPEAN_UNION);
        assertEquals("Netherlands", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceNonEu() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(Jurisdiction.NON_EU);
        assertEquals("Panama", addressServiceCountryOfResidence);
    }

    private void assertAddress(Address address, String addressLine1, String addressLine2,
                               String country, String locality, String postalCode) {
        assertEquals(addressLine1, address.getAddressLine1());
        assertEquals(addressLine2, address.getAddressLine2());
        assertEquals(country, address.getCountry());
        assertEquals(locality, address.getLocality());
        assertEquals(postalCode, address.getPostalCode());
    }
}