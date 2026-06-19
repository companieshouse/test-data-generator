package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getAddressForAllJurisdictions(JurisdictionType jurisdiction, String addressLine1,
                                   String addressLine2, String country, String locality,
                                   String postalCode) {
        Address address = addressService.getAddress(jurisdiction);
        assertAddress(address, addressLine1, addressLine2, country, locality, postalCode);
    }

    @ParameterizedTest
    @MethodSource("countryOfResidenceByJurisdiction")
    void getCountryOfResidenceByJurisdiction(JurisdictionType jurisdiction, String expectedCountryOfResidence) {
        String actualCountryOfResidence = addressService.getCountryOfResidence(jurisdiction);
        assertEquals(expectedCountryOfResidence, actualCountryOfResidence);
    }

    private void assertAddress(Address address, String addressLine1, String addressLine2,
                               String country, String locality, String postalCode) {
        assertEquals(addressLine1, address.getAddressLine1());
        assertEquals(addressLine2, address.getAddressLine2());
        assertEquals(country, address.getCountry());
        assertEquals(locality, address.getLocality());
        assertEquals(postalCode, address.getPostalCode());
    }

    private static Stream<Arguments> addressByJurisdiction() {
        return Stream.of(
                Arguments.of(JurisdictionType.ENGLAND_WALES, "Old Admiralty Building", "Admiralty Place",
                        "United Kingdom", "London", "SW1A 2DY"),
                Arguments.of(JurisdictionType.SCOTLAND, "Queen Elizabeth House", "1 Sibbald Walk",
                        "United Kingdom", "Edinburgh", "EH8 8FT"),
                Arguments.of(JurisdictionType.NI, "Erskine House", "20-32 Chichester Street",
                        "United Kingdom", "Belfast", "BT1 4GF"),
                Arguments.of(JurisdictionType.WALES, "Tŷ William Morgan", "6 Central Square",
                        "United Kingdom", "Cardiff", "CF10 1EP"),
                Arguments.of(JurisdictionType.UNITED_KINGDOM, "Gordon Cummins Hwy",
                        "Grantley Adams International Airport", "Barbados", "Christ Church", "123125"),
                Arguments.of(JurisdictionType.ENGLAND, "4th Floor, The Linen Hall",
                        "162-168 Regent Street", "United Kingdom", "London", "W1B 5TF"),
                Arguments.of(JurisdictionType.EUROPEAN_UNION, "Schiphol Boulevard Tower 403 Tower C-4",
                        "1118bk Schiphol", "Netherlands", "Amsterdam", "123123"),
                Arguments.of(JurisdictionType.NON_EU, "Edificio Salduba Tercer Piso", "Calle 53 Este",
                        "Panama", "Marbella", "123124")
        );
    }

    private static Stream<Arguments> countryOfResidenceByJurisdiction() {
        return Stream.of(
                Arguments.of(JurisdictionType.ENGLAND_WALES, "England"),
                Arguments.of(JurisdictionType.SCOTLAND, "Scotland"),
                Arguments.of(JurisdictionType.NI, "Northern Ireland"),
                Arguments.of(JurisdictionType.UNITED_KINGDOM, "Barbados"),
                Arguments.of(JurisdictionType.ENGLAND, "England"),
                Arguments.of(JurisdictionType.WALES, "Wales"),
                Arguments.of(JurisdictionType.EUROPEAN_UNION, "Netherlands"),
                Arguments.of(JurisdictionType.NON_EU, "Panama")
        );
    }
}
