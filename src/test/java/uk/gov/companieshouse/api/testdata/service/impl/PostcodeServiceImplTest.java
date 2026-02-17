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
    void testPostcodesForInvalidCountry() {
        String invalidCountry = "invalid-country";
        try {
            postcodeService.getPostcodeByCountry(invalidCountry);
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: invalid-country", e.getMessage());
        }
    }

    @Test
    void testPostcodesForEmptyCountry() {
        try {
            postcodeService.getPostcodeByCountry("");
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: ", e.getMessage());
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
                list.stream().allMatch(p -> p.getPostcode() != null && p.getPostcode().getStripped().startsWith("CF")),
                "All results should start with CF"
        );
        assertTrue(list.stream().allMatch(p -> p.getBuildingNumber() != null), "buildingNumber must be non-null");
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

        doReturn(List.of(valid, nullPostcode, nullBuilding)).when(postcodeService).loadAllPostcodes();

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);
        assertEquals(1, result.size());
        assertTrue(result.stream().allMatch(p -> p.getPostcode() != null && p.getBuildingNumber() != null));
        assertTrue(result.contains(valid));
    }

    @Test
    void testIOExceptionWhenReadingPostcodesJsonReturnsException() {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl(randomService));
        // Create an InputStream that throws IOException on read
        var faultyStream = new java.io.InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("Simulated IO error");
            }
        };
        doReturn(faultyStream).when(service).getPostcodesResourceStream();
        try {
            service.loadAllPostcodes();
        } catch (RuntimeException e) {
            assertEquals("java.io.IOException: Simulated IO error", e.getMessage());
        }
    }

    @Test
    void testLoadAllPostcodesReturnsEmptyListWhenInputStreamIsNull() {
        PostcodeServiceImpl service = spy(new PostcodeServiceImpl(randomService));
        doReturn(null).when(service).getPostcodesResourceStream();

        List<Postcodes> result = service.loadAllPostcodes();
        assertTrue(result.isEmpty(), "Should return empty list when inputStream is null");
    }
}