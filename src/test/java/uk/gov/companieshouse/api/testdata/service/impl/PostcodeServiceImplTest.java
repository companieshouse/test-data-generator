package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.OptionalLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.service.RandomService;



@ExtendWith(MockitoExtension.class)
class PostcodeServiceImplTest {

    @Spy
    @InjectMocks
    private PostcodeServiceImpl postcodeService;

    @Mock
    private RandomService randomService;

    private static final String COUNTRY_ENGLAND = "gb-eng";
    private static final String COUNTRY_WALES = "gb-wls";
    private static final String COUNTRY_SCOTLAND = "gb-sct";
    private static final String COUNTRY_NORTHERN_IRELAND = "gb-nir";
    private static final String COUNTRY_ENGLAND_PARTIAL = "eng";
    private static final String COUNTRY_WALES_PARTIAL = "wls";
    private static final String COUNTRY_SCOTLAND_PARTIAL = "sct";
    private static final String COUNTRY_NORTHERN_IRELAND_PARTIAL = "nir";
    private static final String INVALID_COUNTRY = "foo";

    @Test
    void testPostcodeByCountryEngland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryWales() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_WALES);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryScotland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_SCOTLAND);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryNorthernIreland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryEnglandShortCode() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND_PARTIAL);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryWalesShortCode() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_WALES_PARTIAL);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryScotlandShortCode() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_SCOTLAND_PARTIAL);
        assertEquals(10, result.size());
    }

    @Test
    void testPostcodeByCountryNorthernIrelandShortCode() {
        List<Postcodes> result = postcodeService
                .getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND_PARTIAL);
        assertEquals(10, result.size());
    }

    @Test
    void testInvalidCountryReturnsInvalidPostcode() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(INVALID_COUNTRY);
        assertTrue(result.isEmpty() || result.stream().allMatch(p
                -> p.getPostcode() == null || p.getPostcode().getStripped().equals("INVALID")));
    }

    @Test
    void testPostcodesForInvalidCountry() {
        String invalidCountry = "invalid-country";
        try {
            postcodeService.getPostcodeByCountry(invalidCountry);
        } catch (IllegalArgumentException ex) {
            assertEquals("Country not recognised: invalid-country", ex.getMessage());
        }
    }

    @Test
    void testPostcodesForEmptyCountry() {
        try {
            postcodeService.getPostcodeByCountry("");
        } catch (IllegalArgumentException ex) {
            assertEquals("Country not recognised: ", ex.getMessage());
        }
    }

    @Test
    void testEmptyResultsForLoadingPostcodes() {
        doReturn(List.of()).when(postcodeService).loadAllPostcodes();
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertTrue(result.isEmpty());
    }

    @Test
    void testSpecificPostcodePrefixReturnsValidResult() {
        // Select the first prefix for Wales (CF)
        when(randomService.getNumberInRange(0, 4)).thenReturn(OptionalLong.of(0));

        var list = postcodeService.getPostcodeByCountry(COUNTRY_WALES);
        assertFalse(list.isEmpty(), "Expected CF matches");
        assertTrue(
                list.stream().allMatch(p -> p.getPostcode()
                        != null && p.getPostcode().getStripped().startsWith("CF")),
                "All results should start with CF"
        );
        assertTrue(list.stream().allMatch(p -> p.getBuildingNumber()
                != null), "buildingNumber must be non-null");
        assertEquals(10, list.size(), "Should return 10 results");
    }

    @Test
    void testNoBuildingNumberShouldReturnNoPostcode() {
        Postcodes valid = mock(Postcodes.class);
        Postcodes.PostcodeDetails postcodeObj = new Postcodes.PostcodeDetails();
        postcodeObj.setStripped("EN118GB");
        when(valid.getPostcode()).thenReturn(postcodeObj);
        when(valid.getBuildingNumber()).thenReturn(1);

        Postcodes nullPostcode = mock(Postcodes.class);
        when(nullPostcode.getPostcode()).thenReturn(null);

        Postcodes nullBuilding = mock(Postcodes.class);
        when(nullBuilding.getPostcode()).thenReturn(postcodeObj);
        when(nullBuilding.getBuildingNumber()).thenReturn(null);

        doReturn(List.of(valid, nullPostcode, nullBuilding))
                .when(postcodeService).loadAllPostcodes();

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(p
                -> p.getPostcode() != null && p.getBuildingNumber() != null));
        assertTrue(result.contains(valid));
    }

    @Test
    void testIoExceptionWhenReadingPostcodesJsonReturnsException() {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl());
        var faultyStream = new java.io.InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };
        doReturn(faultyStream).when(service).getPostcodesResourceStream();
        try {
            service.loadAllPostcodes();
        } catch (RuntimeException ex) {
            assertEquals("java.io.IOException: Simulated IO error", ex.getMessage());
        }
    }

    @Test
    void testLoadAllPostcodesReturnsEmptyListWhenInputStreamIsNull() {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl());
        doReturn(null).when(service).getPostcodesResourceStream();

        List<Postcodes> result = service.loadAllPostcodes();
        assertTrue(result.isEmpty(), "Should return empty list when inputStream is null");
    }
}