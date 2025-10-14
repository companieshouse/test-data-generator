package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.OptionalLong;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodeRepository;

@ExtendWith(MockitoExtension.class)
class PostcodeServiceImplTest {
    @Mock
    private PostcodeRepository postcodeRepository;
    @Mock
    private RandomServiceImpl randomService;

    @InjectMocks
    private PostcodeServiceImpl postcodeService;

    private static final String COUNTRY_ENGLAND = "gb-eng";
    private static final String COUNTRY_WALES = "gb-wls";
    private static final String COUNTRY_SCOTLAND = "gb-sct";
    private static final String COUNTRY_NORTHERN_IRELAND = "gb-nir";

    @Test
    void testGetPostcodeByCountryPostcodesForEngland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertEquals("E1 6AN", result.stream().findFirst().get().getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                anyList(),
                any()
        );
    }

    @Test
    void testGetPostcodeByCountryPostcodesForWales() {
        List<Postcodes> mockPostcodes = createMockPostcodes("CF10 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_WALES);

        assertEquals("CF10 1AA", result.stream().findFirst().get().getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                anyList(),
                any()
        );
    }

    @Test
    void testGetPostcodeByCountryPostcodesForScotland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("EH1 1BB");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_SCOTLAND);

        assertEquals("EH1 1BB", result.stream().findFirst().get().getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                anyList(),
                any()
        );
    }

    @Test
    void testGetPostcodeByCountryPostcodesForNorthernIreland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("BT1 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);

        assertEquals("BT1 1AA", result.stream().findFirst().get().getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                anyList(),
                any()
        );
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
    void testCachingBehavior() {
        List<Postcodes> mockPostcodes = createMockPostcodes("BT1 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        // First call to populate the cache
        List<Postcodes> result1 = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertEquals(1, result1.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());

        // Second call should use the cache
        List<Postcodes> result2 = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertEquals(1, result2.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testEmptyResults() {
        String prefix = "BT";
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(List.of());

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_NORTHERN_IRELAND);
        assertTrue(result.isEmpty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testMultiplePrefixes() {
        String country = "gb-wls";
        List<Postcodes> mockPostcodes = createMockPostcodes("CF1 1AA");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        List<Postcodes> result = postcodeService.getPostcodeByCountry(country);
        assertEquals(1, result.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testRandomSelection() {
        String country = "gb-eng";
        List<Postcodes> mockPostcodes = createMockPostcodes("AL1 1AA");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        when(randomService.getNumberInRange(anyInt(), anyInt())).thenReturn(OptionalLong.of(0));

        List<Postcodes> result = postcodeService.getPostcodeByCountry(country);
        assertEquals(1, result.size());
        verify(randomService, times(1)).getNumberInRange(anyInt(), anyInt());
    }

    @Test
    void testRandomIndexSelection() {
        when(randomService.getNumberInRange(0, 88)).thenReturn(OptionalLong.of(1));
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertEquals(1, result.size());
        verify(randomService, times(1)).getNumberInRange(0, 88);
    }

    @Test
    void testSkippingAlreadyTriedIndices() {
        when(randomService.getNumberInRange(0, 88)).thenReturn(OptionalLong.of(0));
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertEquals(1, result.size());
        verify(randomService, times(1)).getNumberInRange(0, 88);
    }

    @Test
    void testBreakingLoopWhenAllIndicesTried() {
        when(randomService.getNumberInRange(0, 88)).thenReturn(OptionalLong.empty());
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(List.of());

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertTrue(result.isEmpty());
        verify(randomService, times(89)).getNumberInRange(0, 88);
    }

    @Test
    void testReturningResultsWhenFound() {
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertEquals(1, result.size());
    }

    @Test
    void testReturningEmptyListWhenNoResultsFound() {
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(List.of());

        List<Postcodes> result = postcodeService.getPostcodeByCountry(COUNTRY_ENGLAND);

        assertTrue(result.isEmpty());
    }

    private List<Postcodes> createMockPostcodes(String prettyPostcode) {
        Postcodes postcode = new Postcodes();
        postcode.setPretty(prettyPostcode);
        return List.of(postcode);
    }
}
