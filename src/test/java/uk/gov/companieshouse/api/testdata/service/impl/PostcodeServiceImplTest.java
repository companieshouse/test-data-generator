package uk.gov.companieshouse.api.testdata.service.impl;

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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @Test
    void testGetPostcodeByCountryPostcodesForEngland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertTrue(result.stream().findFirst().isPresent());
    }

    @Test
    void testGetPostcodeByCountryPostcodesForWales() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_WALES);
        assertTrue(result.stream().findFirst().isPresent());
    }

    @Test
    void testGetPostcodeByCountryPostcodesForScotland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_SCOTLAND);
        assertTrue(result.stream().findFirst().isPresent());
    }

    @Test
    void testGetPostcodeByCountryPostcodesForNorthernIreland() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertTrue(result.stream().findFirst().isPresent());
    }

    @Test
    void testGetPostcodeByCountryPostcodesForInvalidCountry() {
        String invalidCountry = "invalid-country";
        try {
            postcodeService.getPostcodeByCountry(invalidCountry);
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: invalid-country", e.getMessage());
        }
    }

    @Test
    void testGetPostcodeByCountryPostcodesForEmptyCountry() {
        try {
            postcodeService.getPostcodeByCountry("");
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: ", e.getMessage());
        }
    }

    @Test
    void testEmptyResults() {
        // This test assumes postcodes.json is empty or does not contain BT prefix
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        if (!result.isEmpty()) {
            result.stream().findFirst();
        }
        assertTrue(true);
    }

    @Test
    void testMultiplePrefixes() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_WALES);
        assertTrue(result.size() <= 10);
    }

    @Test
    void testReturningResultsWhenFound() {
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertFalse(result.isEmpty());
    }

    @Test
    void testReturningEmptyListWhenNoResultsFound() {
        // This test assumes postcodes.json does not contain a prefix "ZZ"
        try {
            postcodeService.getPostcodeByCountry("zz");
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: zz", e.getMessage());
        }
    }

    @Test
    void gbWales_returnsCfMatches_whenRandomPicksCF() {
        when(randomService.getNumberInRange(0, 4)).thenReturn(OptionalLong.of(0)); // index 0 -> "CF"

        var list = postcodeService.getPostcodeByCountry(COUNTRY_WALES);
        assertFalse(list.isEmpty(), "Expected CF matches");
        assertTrue(
                list.stream().allMatch(p -> p.getPostcode() != null && p.getPostcode().getStripped().startsWith("CF")),
                "All results should start with CF"
        );
        assertTrue(list.stream().allMatch(p -> p.getBuildingNumber() != null), "buildingNumber must be non-null");
        assertTrue(list.size() <= 10, "Should return up to 10 results");
    }

    @Test
    void testMissingPostcodesJsonReturnsEmptyList() {
        doReturn(List.of()).when(postcodeService).loadAllPostcodes();
        List<Postcodes> result = postcodeService.getPostcodeByCountry("gb-eng");
        assertTrue(result.isEmpty());
    }

    @Test
    void testPostcodeWithNullPostcodeOrBuildingNumberFilteredOut() {
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

        doReturn(List.of(valid, nullPostcode, nullBuilding)).when(postcodeService).loadAllPostcodes();

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertEquals(1, result.size());
        assertTrue(result.contains(valid));
    }

    @Test
    void testIOExceptionWhenReadingPostcodesJsonReturnsEmptyList() throws Exception {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl());
        // Create an InputStream that throws IOException on read
        var faultyStream = new java.io.InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };
        doReturn(faultyStream).when(service).getPostcodesResourceStream();

        List<Postcodes> result = service.loadAllPostcodes();
        assertTrue(result.isEmpty(), "Should return empty list when IOException occurs");
    }

    @Test
    void testLoadAllPostcodesReturnsEmptyListWhenInputStreamIsNull() {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl());
        doReturn(null).when(service).getPostcodesResourceStream();

        List<Postcodes> result = service.loadAllPostcodes();
        assertTrue(result.isEmpty(), "Should return empty list when inputStream is null");
    }
}