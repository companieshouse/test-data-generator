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
        assertEquals("Companies House,4th Floor, The Linen Hall", address.getAddressLine1());
        assertEquals("162-168 Regent Street", address.getAddressLine2());
        assertEquals("United Kingdom", address.getCountry());
        assertEquals("London", address.getLocality());
        assertEquals("W1B 5TF", address.getPostalCode());
    }

    @Test
    void getAddressForJurisdictionEuropeanUnion() {
        Address address = addressService.getAddress(Jurisdiction.EUROPEAN_UNION);
        assertEquals("Schiphol Boulevard Tower 403 Tower C-4", address.getAddressLine1());
        assertEquals("1118bk Schiphol", address.getAddressLine2());
        assertEquals("Amsterdam", address.getLocality());
        assertEquals("Netherlands", address.getCountry());
    }

    @Test
    void getAddressForJurisdictionNonEu() {
        Address address = addressService.getAddress(Jurisdiction.NON_EU);
        assertEquals("Edificio Salduba Tercer Piso", address.getAddressLine1());
        assertEquals("Calle 53 Este", address.getAddressLine2());
        assertEquals("Marbella", address.getLocality());
        assertEquals("Panama", address.getCountry());
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
}