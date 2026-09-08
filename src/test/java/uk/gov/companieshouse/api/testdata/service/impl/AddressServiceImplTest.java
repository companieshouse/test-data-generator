package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;

class AddressServiceImplTest {

    private AddressServiceImpl addressService;

    @BeforeEach
    void init() {
        this.addressService = new AddressServiceImpl();
    }

    @ParameterizedTest
    @MethodSource("addressByJurisdiction")
    void getAddressForAllJurisdictions(JurisdictionType jurisdiction, String country, String postalCode) {
        Address address = addressService.getAddress(jurisdiction);
        assertAddress(address, jurisdiction, country, postalCode);
    }

    @ParameterizedTest
    @MethodSource("countryOfResidenceByJurisdiction")
    void getCountryOfResidenceByJurisdiction(JurisdictionType jurisdiction, String expectedCountryOfResidence) {
        String actualCountryOfResidence = addressService.getCountryOfResidence(jurisdiction);
        assertEquals(expectedCountryOfResidence, actualCountryOfResidence);
    }

    private void assertAddress(
            Address address, JurisdictionType jurisdiction, String country, String postalCode) {
        assertNotNull(address);
        assertNotNull(address.getAddressLine1());
        assertNotNull(address.getAddressLine2());
        if (jurisdiction == JurisdictionType.NON_EU) {
            assertNonEuCountry(address.getCountry());
            assertNonEuPostcode(address.getPostalCode());
        } else {
            assertEquals(country, address.getCountry());
            assertEquals(postalCode, address.getPostalCode());
        }
        assertNotNull(address.getLocality());
    }

    private void assertNonEuCountry(String country) {
        boolean valid = "Panama".equals(country) || "Canada".equals(country) || "Australia".equals(country);
        assertTrue(valid);
    }

    private void assertNonEuPostcode(String postalCode) {
        boolean valid = "0000-0000".equals(postalCode) || "Z9Z 9Z9".equals(postalCode) || "0000".equals(postalCode);
        assertTrue(valid);
    }

    private static Stream<Arguments> addressByJurisdiction() {
        return Stream.of(
                Arguments.of(JurisdictionType.ENGLAND_WALES, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.SCOTLAND, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.NI, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.WALES, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.UNITED_KINGDOM, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.ENGLAND, "United Kingdom", "ZZ1 1ZZ"),
                Arguments.of(JurisdictionType.EUROPEAN_UNION, "Netherlands", "0000 ZZ"),
                Arguments.of(JurisdictionType.NON_EU, "", "0000-0000")
        );
    }

    private static Stream<Arguments> countryOfResidenceByJurisdiction() {
        return Stream.of(
                Arguments.of(JurisdictionType.ENGLAND_WALES, "England"),
                Arguments.of(JurisdictionType.SCOTLAND, "Scotland"),
                Arguments.of(JurisdictionType.NI, "Northern Ireland"),
                Arguments.of(JurisdictionType.UNITED_KINGDOM, "United Kingdom"),
                Arguments.of(JurisdictionType.ENGLAND, "England"),
                Arguments.of(JurisdictionType.WALES, "Wales"),
                Arguments.of(JurisdictionType.EUROPEAN_UNION, "Netherlands"),
                Arguments.of(JurisdictionType.NON_EU, "Panama")
        );
    }
}
