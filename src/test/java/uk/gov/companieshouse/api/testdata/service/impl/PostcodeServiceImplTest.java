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
    void testGetPostcodesForEngland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");

        when(postcodeRepository.findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.get(COUNTRY_ENGLAND);

        assertEquals(1, result.size());
        assertEquals("E1 6AN", result.get(0).getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForWales() {
        List<Postcodes> mockPostcodes = createMockPostcodes("CF10 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.get(COUNTRY_WALES);

        assertEquals(1, result.size());
        assertEquals("CF10 1AA", result.get(0).getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForScotland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("EH1 1BB");

        when(postcodeRepository.findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.get(COUNTRY_SCOTLAND);

        assertEquals(1, result.size());
        assertEquals("EH1 1BB", result.get(0).getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForNorthernIreland() {
        List<Postcodes> mockPostcodes = createMockPostcodes("BT1 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);
        List<Postcodes> result = postcodeService.get(COUNTRY_NORTHERN_IRELAND);

        assertEquals(1, result.size());
        assertEquals("BT1 1AA", result.get(0).getPretty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForInvalidCountry() {
        String invalidCountry = "invalid-country";

        try {
            postcodeService.get(invalidCountry);
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: invalid-country", e.getMessage());
        }
    }

    @Test
    void testGetPostcodesForEmptyCountry() {
        try {
            postcodeService.get("");
        } catch (IllegalArgumentException e) {
            assertEquals("Country not recognised: ", e.getMessage());
        }
    }

    @Test
    void testCachingBehavior() {
        String prefix = "BT";
        List<Postcodes> mockPostcodes = createMockPostcodes("BT1 1AA");

        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        // First call to populate the cache
        List<Postcodes> result1 = postcodeService.get(COUNTRY_NORTHERN_IRELAND);
        assertEquals(1, result1.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());

        // Second call should use the cache
        List<Postcodes> result2 = postcodeService.get(COUNTRY_NORTHERN_IRELAND);
        assertEquals(1, result2.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testEmptyResults() {
        String prefix = "BT";
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(List.of());

        List<Postcodes> result = postcodeService.get(COUNTRY_NORTHERN_IRELAND);
        assertTrue(result.isEmpty());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testMultiplePrefixes() {
        String country = "gb-wls";
        List<Postcodes> mockPostcodes = createMockPostcodes("CF1 1AA");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);

        List<Postcodes> result = postcodeService.get(country);
        assertEquals(1, result.size());
        verify(postcodeRepository, times(1)).findByPostcodePrefixContaining(anyList(), any());
    }

    @Test
    void testRandomSelection() {
        String country = "gb-eng";
        List<Postcodes> mockPostcodes = createMockPostcodes("AL1 1AA");
        when(postcodeRepository.findByPostcodePrefixContaining(anyList(), any())).thenReturn(mockPostcodes);
        when(randomService.getNumberInRange(anyInt(), anyInt())).thenReturn(OptionalLong.of(0));

        List<Postcodes> result = postcodeService.get(country);
        assertEquals(1, result.size());
        verify(randomService, times(1)).getNumberInRange(anyInt(), anyInt());
    }

    private List<Postcodes> createMockPostcodes(String prettyPostcode) {
        Postcodes postcode = new Postcodes();
        postcode.setPretty(prettyPostcode);
        return List.of(postcode);
    }
}
