package uk.gov.companieshouse.api.testdata.service.address.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Locale;
import java.util.stream.Stream;

import net.datafaker.Faker;
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
import uk.gov.companieshouse.api.testdata.service.address.profile.EuProfile;
import uk.gov.companieshouse.api.testdata.service.address.profile.LocalityCluster;
import uk.gov.companieshouse.api.testdata.service.address.profile.NonEuProfile;

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
    void getAddressForAllJurisdictions(JurisdictionType jurisdiction) {
        Address address = addressService.getAddress(jurisdiction);
        assertAddress(address, jurisdiction);
    }

    @ParameterizedTest
    @MethodSource("countryOfResidenceByJurisdiction")
    void getCountryFromSelectedProfileByJurisdiction(JurisdictionType jurisdiction, java.util.List<String> expectedCountries) {
        String actualCountryOfResidence = addressService.getCountryFromSelectedProfile(jurisdiction);
        assertTrue(expectedCountries.contains(actualCountryOfResidence),
                "Expected one of " + expectedCountries + " but got " + actualCountryOfResidence);
    }

    @ParameterizedTest
    @MethodSource("countryOfResidenceByJurisdiction")
    void getCountryFromSelectedProfileConsistentAcrossCalls(JurisdictionType jurisdiction, java.util.List<String> expectedCountries) {
        String country1 = addressService.getCountryFromSelectedProfile(jurisdiction);
        String country2 = addressService.getCountryFromSelectedProfile(jurisdiction);
        assertEquals(country1, country2, "Country should be consistent across calls for " + jurisdiction);
        assertTrue(expectedCountries.contains(country1), 
                "Expected one of " + expectedCountries + " but got " + country1);
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
        assertTrue(isValidUkCountry(address.getCountry()),
                "Address country should be England or Wales, but was: " + address.getCountry());
        
        // Get country from selected profile
        String selectedCountry = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertEquals(address.getCountry(), selectedCountry, "Address country should match selected profile country");
    }

    @Test
    @DisplayName("For UNITED_KINGDOM, an overseas country should be consistent across multiple calls")
    void testUnitedKingdomCountryConsistency() {
        mockContext.clear();
        
        // First call selects and caches a country
        String country1 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertOverseasCountry(country1);
        
        // Subsequent calls should return the same cached country
        String country2 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertEquals(country1, country2, "Country should remain consistent across calls");
        
        String country3 = addressService.getCountryFromSelectedProfile(JurisdictionType.UNITED_KINGDOM);
        assertEquals(country1, country3, "Country should remain consistent across multiple calls");
    }

    @Test
    @DisplayName("For UNITED_KINGDOM, an EU profile generates an EU address")
    void testUnitedKingdomUsesEuProfile() {
        mockContext.clear();
        mockContext.setSelectedUnitedKingdomProfileType(JurisdictionType.EUROPEAN_UNION);

        UsualResidentialAddress address =
                addressService.getUsualResidentialAddress(JurisdictionType.UNITED_KINGDOM);

        assertEuCountry(address.getCountry());
        assertGeneratedPostcode(address.getPostalCode());
    }

    @Test
    @DisplayName("For UNITED_KINGDOM, a non-EU profile generates a non-EU address")
    void testUnitedKingdomUsesNonEuProfile() {
        mockContext.clear();
        mockContext.setSelectedUnitedKingdomProfileType(JurisdictionType.NON_EU);

        UsualResidentialAddress address =
                addressService.getUsualResidentialAddress(JurisdictionType.UNITED_KINGDOM);

        assertNonEuCountry(address.getCountry());
        assertGeneratedPostcode(address.getPostalCode());
    }

    @ParameterizedTest
    @MethodSource("profilePostcodeFormats")
    void generatesPostcodeInProfileFormat(
            String country,
            JurisdictionType jurisdiction,
            String locality,
            String region,
            String postcodePattern) {
        LocalityCluster[] clusters = {
                new LocalityCluster(locality, "AREA", region)
        };
        Faker faker = new Faker(Locale.UK);
        if (jurisdiction == JurisdictionType.EUROPEAN_UNION) {
            mockContext.setEuProfile(new EuProfile(country, faker, clusters));
        } else {
            mockContext.setNonEuProfile(new NonEuProfile(country, faker, clusters));
        }

        UsualResidentialAddress address = addressService.getUsualResidentialAddress(jurisdiction);

        assertTrue((address.getAddressLine1() + " " + address.getAddressLine2())
                .contains("TEST DATA"));
        assertTrue(address.getPostalCode().matches(postcodePattern),
                "Unexpected " + country + " postcode: " + address.getPostalCode());
    }

    @Test
    @DisplayName("For ENGLAND_WALES, multiple address calls should generate from same country")
    void testEnglandWalesAddressesFromSameCountry() {
        mockContext.clear();
        
        // Generate multiple addresses with ENGLAND_WALES jurisdiction
        UsualResidentialAddress addr1 = addressService.getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
        UsualResidentialAddress addr2 = addressService.getUsualResidentialAddress(JurisdictionType.ENGLAND_WALES);
        
        // Both should be valid UK countries
        assertTrue(isValidUkCountry(addr1.getCountry()),
                "Address 1 country should be a UK country, but was: " + addr1.getCountry());
        assertTrue(isValidUkCountry(addr2.getCountry()),
                "Address 2 country should be a UK country, but was: " + addr2.getCountry());
        
        // Both should be from the same country (cached in context)
        assertEquals(addr1.getCountry(), addr2.getCountry(),
                "Both addresses should be from the same cached country");
        
        // Get the selected country
        String selectedCountry = addressService.getCountryFromSelectedProfile(JurisdictionType.ENGLAND_WALES);
        assertEquals(addr1.getCountry(), selectedCountry, "Address country should match selected profile country");
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

    private void assertAddress(Address address, JurisdictionType jurisdiction) {
        assertNotNull(address);
        assertNotNull(address.getAddressLine1());
        assertNotNull(address.getAddressLine2());
        assertTrue((address.getAddressLine1() + " " + address.getAddressLine2())
                .contains("TEST DATA"));
        if (jurisdiction == JurisdictionType.NON_EU) {
            assertNonEuCountry(address.getCountry());
        } else if (jurisdiction == JurisdictionType.EUROPEAN_UNION) {
            assertEuCountry(address.getCountry());
        } else if (jurisdiction == JurisdictionType.UNITED_KINGDOM) {
            assertOverseasCountry(address.getCountry());
        } else if (isUkJurisdiction(jurisdiction)) {
            assertTrue(isValidUkCountry(address.getCountry()),
                    "Country should be one of the UK countries, but was: " + address.getCountry());
            assertTrue(address.getPostalCode().matches("[A-Z]{1,2}\\d{1,2} \\d[A-Z]{2}"),
                    "Unexpected UK postcode: " + address.getPostalCode());
        }
        assertGeneratedPostcode(address.getPostalCode());
        assertNotNull(address.getLocality());
    }

    private boolean isUkJurisdiction(JurisdictionType jurisdiction) {
        return jurisdiction == JurisdictionType.ENGLAND_WALES ||
               jurisdiction == JurisdictionType.SCOTLAND ||
               jurisdiction == JurisdictionType.NI ||
               jurisdiction == JurisdictionType.WALES ||
               jurisdiction == JurisdictionType.ENGLAND;
    }

    private void assertEuCountry(String country) {
        boolean valid = "Netherlands".equals(country) || "Germany".equals(country) || "France".equals(country)
                || "Spain".equals(country) || "Italy".equals(country) || "Poland".equals(country)
                || "Malta".equals(country) || "Cyprus".equals(country);
        assertTrue(valid, "Country should be one of the EU countries, but was: " + country);
    }

    private void assertNonEuCountry(String country) {
        boolean valid = "Panama".equals(country) || "Canada".equals(country) || "Australia".equals(country)
                || "Jersey".equals(country) || "Guernsey".equals(country) || "Isle of Man".equals(country)
                || "Bermuda".equals(country);
        assertTrue(valid, "Country should be one of the non-EU countries, but was: " + country);
    }

    private void assertOverseasCountry(String country) {
        boolean valid = "Netherlands".equals(country) || "Germany".equals(country) || "France".equals(country)
                || "Spain".equals(country) || "Italy".equals(country) || "Poland".equals(country)
                || "Malta".equals(country) || "Cyprus".equals(country) || "Panama".equals(country)
                || "Canada".equals(country) || "Australia".equals(country) || "Jersey".equals(country)
                || "Guernsey".equals(country) || "Isle of Man".equals(country) || "Bermuda".equals(country);
        assertTrue(valid, "Country should be an EU or non-EU overseas country, but was: " + country);
    }

    private void assertGeneratedPostcode(String postalCode) {
        assertNotNull(postalCode);
        assertTrue(!postalCode.isBlank(), "Postcode should be generated");
    }

    private boolean isValidUkCountry(String country) {
        return "England".equals(country) || "Wales".equals(country) || 
               "Scotland".equals(country) || "Northern Ireland".equals(country);
    }

    private static Stream<JurisdictionType> addressByJurisdiction() {
        return Stream.of(JurisdictionType.values());
    }

    private static Stream<Arguments> countryOfResidenceByJurisdiction() {
        return Stream.of(
                Arguments.of(JurisdictionType.ENGLAND_WALES, java.util.List.of("England", "Wales")),
                Arguments.of(JurisdictionType.SCOTLAND, java.util.List.of("Scotland")),
                Arguments.of(JurisdictionType.NI, java.util.List.of("Northern Ireland")),
                Arguments.of(JurisdictionType.UNITED_KINGDOM, java.util.List.of(
                        "Netherlands", "Germany", "France", "Spain", "Italy", "Poland", "Malta", "Cyprus",
                        "Panama", "Canada", "Australia", "Jersey", "Guernsey", "Isle of Man", "Bermuda")),
                Arguments.of(JurisdictionType.ENGLAND, java.util.List.of("England")),
                Arguments.of(JurisdictionType.WALES, java.util.List.of("Wales")),
                Arguments.of(JurisdictionType.EUROPEAN_UNION, java.util.List.of("Netherlands", "Germany", "France", "Spain", "Italy", "Poland", "Malta", "Cyprus")),
                Arguments.of(JurisdictionType.NON_EU, java.util.List.of("Panama", "Canada", "Australia", "Jersey", "Guernsey", "Isle of Man", "Bermuda"))
        );
    }

    private static Stream<Arguments> profilePostcodeFormats() {
        return Stream.of(
                Arguments.of("Netherlands", JurisdictionType.EUROPEAN_UNION,
                        "AMSTERDAM", "NOORD-HOLLAND", "[1-9]\\d{3} [A-Z]{2}"),
                Arguments.of("Germany", JurisdictionType.EUROPEAN_UNION,
                        "BERLIN", "BERLIN STATE", "\\d{5}"),
                Arguments.of("Germany", JurisdictionType.EUROPEAN_UNION,
                        "DRESDEN", "SAXONY", "01(0[6-9]|[12]\\d|3[0-2])\\d"),
                Arguments.of("Germany", JurisdictionType.EUROPEAN_UNION,
                        "MUNICH", "BAVARIA", "8\\d{4}"),
                Arguments.of("France", JurisdictionType.EUROPEAN_UNION,
                        "PARIS", "ILE-DE-FRANCE", "\\d{5}"),
                Arguments.of("Spain", JurisdictionType.EUROPEAN_UNION,
                        "MADRID", "COMMUNITY OF MADRID", "(0[1-9]|[1-4]\\d|5[0-2])\\d{3}"),
                Arguments.of("Italy", JurisdictionType.EUROPEAN_UNION,
                        "ROME", "LAZIO", "\\d{5}"),
                Arguments.of("Poland", JurisdictionType.EUROPEAN_UNION,
                        "WARSAW", "MAZOVIA", "\\d{2}-\\d{3}"),
                Arguments.of("Malta", JurisdictionType.EUROPEAN_UNION,
                        "VALLETTA", "MALTA ISLAND", "VLT \\d{4}"),
                Arguments.of("Cyprus", JurisdictionType.EUROPEAN_UNION,
                        "NICOSIA", "CYPRUS ISLAND", "\\d{4}"),
                Arguments.of("Panama", JurisdictionType.NON_EU,
                        "PANAMA CITY", "PANAMA PROVINCE", "\\d{4}"),
                Arguments.of("Canada", JurisdictionType.NON_EU,
                        "TORONTO", "ONTARIO", "M\\d[A-Z] \\d[A-Z]\\d"),
                Arguments.of("Australia", JurisdictionType.NON_EU,
                        "SYDNEY", "NEW SOUTH WALES", "2\\d{3}"),
                Arguments.of("Jersey", JurisdictionType.NON_EU,
                        "ST. HELIER", "JERSEY ISLAND", "JE[12] \\d[A-Z]{2}"),
                Arguments.of("Jersey", JurisdictionType.NON_EU,
                        "ST. BRELADE", "JERSEY ISLAND", "JE3 \\d[A-Z]{2}"),
                Arguments.of("Guernsey", JurisdictionType.NON_EU,
                        "ST. PETER PORT", "GUERNSEY ISLAND", "GY1 \\d[A-Z]{2}"),
                Arguments.of("Guernsey", JurisdictionType.NON_EU,
                        "VALE", "GUERNSEY ISLAND", "GY3 \\d[A-Z]{2}"),
                Arguments.of("Isle of Man", JurisdictionType.NON_EU,
                        "DOUGLAS", "ISLE OF MAN", "IM[12] \\d[A-Z]{2}"),
                Arguments.of("Isle of Man", JurisdictionType.NON_EU,
                        "RAMSEY", "ISLE OF MAN", "IM8 \\d[A-Z]{2}"),
                Arguments.of("Bermuda", JurisdictionType.NON_EU,
                        "HAMILTON", "PEMBROKE",
                        "HM \\d{2}"),
                Arguments.of("Bermuda", JurisdictionType.NON_EU,
                        "SMITHS", "SMITHS", "FL \\d{2}"),
                Arguments.of("Bermuda", JurisdictionType.NON_EU,
                        "SOUTHAMPTON", "SOUTHAMPTON", "SN \\d{2}"),
                Arguments.of("Netherlands", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "[1-9]\\d{3} [A-Z]{2}"),
                Arguments.of("Germany", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "\\d{5}"),
                Arguments.of("France", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "\\d{5}"),
                Arguments.of("Spain", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "(0[1-9]|[1-4]\\d|5[0-2])\\d{3}"),
                Arguments.of("Italy", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "\\d{5}"),
                Arguments.of("Poland", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "\\d{2}-\\d{3}"),
                Arguments.of("Malta", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "[A-Z]{3} \\d{4}"),
                Arguments.of("Cyprus", JurisdictionType.EUROPEAN_UNION,
                        "UNMAPPED", "UNMAPPED", "\\d{4}"),
                Arguments.of("Panama", JurisdictionType.NON_EU,
                        "UNMAPPED", "UNMAPPED", "\\d{4}"),
                Arguments.of("Canada", JurisdictionType.NON_EU,
                        "UNMAPPED", "UNMAPPED", "[A-Z]\\d[A-Z] \\d[A-Z]\\d"),
                Arguments.of("Australia", JurisdictionType.NON_EU,
                        "UNMAPPED", "UNMAPPED", "\\d{4}"),
                Arguments.of("Unmapped country", JurisdictionType.NON_EU,
                        "UNMAPPED", "UNMAPPED", ".+")
        );
    }
}
