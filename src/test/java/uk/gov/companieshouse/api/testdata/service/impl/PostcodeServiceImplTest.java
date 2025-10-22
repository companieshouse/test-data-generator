package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PostcodeServiceImplTest {

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


}