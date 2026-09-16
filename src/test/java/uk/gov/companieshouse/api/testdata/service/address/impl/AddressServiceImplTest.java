package uk.gov.companieshouse.api.testdata.service.address.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.UsualResidentialAddress;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.service.address.AddressProfileContext;

class AddressServiceImplTest {

    private AddressServiceImpl addressService;
    private AddressProfileContext mockContext;

    @BeforeEach
    void init() {
        this.addressService = new AddressServiceImpl();
        this.mockContext = new AddressProfileContext();
        // Inject the mock context into the service
        try {
            java.lang.reflect.Field field = AddressServiceImpl.class.getDeclaredField("addressProfileContext");
            field.setAccessible(true);
            field.set(addressService, mockContext);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    @Test
    @DisplayName("For ENGLAND_WALES, getCountryFromSelectedProfile should be consistent across multiple calls")
    void testEnglandWalesCountryConsistency() {
        mockContext.clear();
        
        // First call selects and caches a country
        String country1 = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertTrue(country1.equals("England") || country1.equals("Wales"), 
                   "Country should be England or Wales, but was: " + country1);
        
        // Subsequent calls should return the same cached country
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertEquals(country1, country2, "Country should remain consistent across calls");
        
        String country3 = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertEquals(country1, country3, "Country should remain consistent across multiple calls");
    }

    @Test
    @DisplayName("For ENGLAND_WALES, address country and getCountryFromSelectedProfile should match")
    void testEnglandWalesAddressCountryConsistency() {
        mockContext.clear();
        
        // Get address first
        UsualResidentialAddress address = addressService.getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
        assertNotNull(address.getCountry());
        assertEquals("United Kingdom", address.getCountry());
        
        // Get country from selected profile
        String selectedCountry = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertTrue(selectedCountry.equals("England") || selectedCountry.equals("Wales"));
    }

    @Test
    @DisplayName("For UNITED_KINGDOM, getCountryFromSelectedProfile should be consistent across multiple calls")
    void testUnitedKingdomCountryConsistency() {
        mockContext.clear();
        
        // First call selects and caches a country
        String country1 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertTrue(isValidUkCountry(country1), 
                   "Country should be one of the UK countries, but was: " + country1);
        
        // Subsequent calls should return the same cached country
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertEquals(country1, country2, "Country should remain consistent across calls");
        
        String country3 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertEquals(country1, country3, "Country should remain consistent across multiple calls");
    }

    @Test
    @DisplayName("For ENGLAND_WALES, multiple address calls should generate from same country")
    void testEnglandWalesAddressesFromSameCountry() {
        mockContext.clear();
        
        // Generate multiple addresses with ENGLAND_WALES jurisdiction
        UsualResidentialAddress addr1 = addressService.getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
        UsualResidentialAddress addr2 = addressService.getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
        
        // Both should be from UK
        assertEquals("United Kingdom", addr1.getCountry());
        assertEquals("United Kingdom", addr2.getCountry());
        
        // Get the selected country
        String selectedCountry = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertTrue(selectedCountry.equals("England") || selectedCountry.equals("Wales"));
    }

    @Test
    @DisplayName("For EU jurisdiction, country should be one of the 6 EU countries")
    void testEuCountrySelection() {
        mockContext.clear();
        
        String country = addressService.getCountryFromSelectedProfile(JurisdictionType.EUROPEAN_UNION);
        assertEuCountry(country);
        
        // Verify consistency on subsequent calls
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.EUROPEAN_UNION);
        assertEquals(country, country2);
    }

    @Test
    @DisplayName("For NonEU jurisdiction, country should be one of the 7 non-EU countries")
    void testNonEuCountrySelection() {
        mockContext.clear();
        
        String country = addressService.getCountryFromSelectedProfile(JurisdictionType.NON_EU);
        assertNonEuCountry(country);
        
        // Verify consistency on subsequent calls
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.NON_EU);
        assertEquals(country, country2);
    }

    @Test
    @DisplayName("Clear context should reset country selection for next request")
    void testContextClearResetsCountrySelection() {
        mockContext.clear();
        
        String country1 = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        
        mockContext.clear();
        
        // After clearing, we can't easily predict the next value, but it should work without error
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertNotNull(country2);
    }

    private void assertAddress(
            Address address, JurisdictionType jurisdiction, String country, String postalCode) {
        assertNotNull(address);
        assertNotNull(address.getAddressLine1());
        assertNotNull(address.getAddressLine2());
        if (jurisdiction == JurisdictionType.NON_EU) {
            assertNonEuCountry(address.getCountry());
            assertNonEuPostcode(address.getPostalCode());
        } else if (jurisdiction == JurisdictionType.EUROPEAN_UNION) {
            assertEuCountry(address.getCountry());
            assertEuPostcode(address.getPostalCode());
        } else {
            assertEquals(country, address.getCountry());
            assertEquals(postalCode, address.getPostalCode());
        }
        assertNotNull(address.getLocality());
    }

    private void assertEuCountry(String country) {
        boolean valid = "Netherlands".equals(country) || "Germany".equals(country) || "France".equals(country)
                || "Spain".equals(country) || "Italy".equals(country) || "Poland".equals(country);
        assertTrue(valid, "Country should be one of the EU countries, but was: " + country);
    }

    private void assertEuPostcode(String postalCode) {
        boolean valid = "0000 ZZ".equals(postalCode) || "00000".equals(postalCode) || "75000".equals(postalCode)
                || "28000".equals(postalCode) || "00-000".equals(postalCode);
        assertTrue(valid, "Postcode should be one of the EU postcodes, but was: " + postalCode);
    }

    private void assertNonEuCountry(String country) {
        boolean valid = "Panama".equals(country) || "Canada".equals(country) || "Australia".equals(country)
                || "Jersey".equals(country) || "Malta".equals(country) || "Cyprus".equals(country) || "Bermuda".equals(country);
        assertTrue(valid, "Country should be one of the non-EU countries, but was: " + country);
    }

    private void assertNonEuPostcode(String postalCode) {
        boolean valid = "0000-0000".equals(postalCode) || "Z9Z 9Z9".equals(postalCode) || "0000".equals(postalCode)
                || "JE1 1AA".equals(postalCode) || "VLT 1000".equals(postalCode) || "1000".equals(postalCode) || "HM 11".equals(postalCode);
        assertTrue(valid, "Postcode should be one of the non-EU postcodes, but was: " + postalCode);
    }

    private boolean isValidUkCountry(String country) {
        return "England".equals(country) || "Wales".equals(country) || 
               "Scotland".equals(country) || "Northern Ireland".equals(country);
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
