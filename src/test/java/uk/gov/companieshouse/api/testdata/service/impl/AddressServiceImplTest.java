package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;


class AddressServiceImplTest {

    private AddressServiceImpl addressService;

    @BeforeEach
    void init() {
        this.addressService = new AddressServiceImpl();
    }

    @Test
    void getAddressForJurisdictionEnglandAndWales() {
        Address address = addressService.getAddress(JurisdictionType.ENGLAND_WALES);
        assertAddress(address, "Old Admiralty Building", "Admiralty Place",
                "United Kingdom", "London", "SW1A 2DY");
    }

    @Test
    void getAddressForJurisdictionScotland() {
        Address address = addressService.getAddress(JurisdictionType.SCOTLAND);
        assertAddress(address, "Queen Elizabeth House", "1 Sibbald Walk",
                "United Kingdom", "Edinburgh", "EH8 8FT");
    }

    @Test
    void getAddressForJurisdictionNI() {
        Address address = addressService.getAddress(JurisdictionType.NI);
        assertAddress(address, "Erskine House", "20-32 Chichester Street",
                "United Kingdom", "Belfast", "BT1 4GF");
    }

    @Test
    void getAddressForJurisdictionWales() {
        Address address = addressService.getAddress(JurisdictionType.WALES);
        assertAddress(address, "Tŷ William Morgan", "6 Central Square",
                "United Kingdom", "Cardiff", "CF10 1EP");
    }

    @Test
    void getAddressForJurisdictionUnitedKingdom() {
        Address address = addressService.getAddress(JurisdictionType.UNITED_KINGDOM);
        assertAddress(address, "Gordon Cummins Hwy", "Grantley Adams International Airport",
                "Barbados", "Christ Church", "123125");
    }

    @Test
    void getAddressForJurisdictionEngland() {
        Address address = addressService.getAddress(JurisdictionType.ENGLAND);
        assertAddress(address, "4th Floor, The Linen Hall",
                "162-168 Regent Street", "United Kingdom", "London", "W1B 5TF");
    }

    @Test
    void getAddressForJurisdictionEuropeanUnion() {
        Address address = addressService.getAddress(JurisdictionType.EUROPEAN_UNION);
        assertAddress(address, "Schiphol Boulevard Tower 403 Tower C-4",
                "1118bk Schiphol", "Netherlands", "Amsterdam", "123123");
    }

    @Test
    void getAddressForJurisdictionNonEu() {
        Address address = addressService.getAddress(JurisdictionType.NON_EU);
        assertAddress(address, "Edificio Salduba Tercer Piso", "Calle 53 Este",
                "Panama", "Marbella", "123124");
    }

    @Test
    void getCountryOfResidenceEnglandWales() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.ENGLAND_WALES);
        assertEquals("England", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceScotland() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.SCOTLAND);
        assertEquals("Scotland", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceNI() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.NI);
        assertEquals("Northern Ireland", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceUnitedKingdom() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.UNITED_KINGDOM);
        assertEquals("Barbados", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceEngland() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.ENGLAND);
        assertEquals("England", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceWales() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.WALES);
        assertEquals("Wales", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceEuropeanUnion() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.EUROPEAN_UNION);
        assertEquals("Netherlands", addressServiceCountryOfResidence);
    }

    @Test
    void getCountryOfResidenceNonEu() {
        String addressServiceCountryOfResidence
                = addressService.getCountryOfResidence(JurisdictionType.NON_EU);
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