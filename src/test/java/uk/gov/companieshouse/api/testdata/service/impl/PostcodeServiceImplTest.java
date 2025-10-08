package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

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
        assertEquals("E1 6AN", result.getFirst().getPretty());
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
        assertEquals("CF10 1AA", result.getFirst().getPretty());
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
        assertEquals("EH1 1BB", result.getFirst().getPretty());
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
        assertEquals("BT1 1AA", result.getFirst().getPretty());
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

    private List<Postcodes> createMockPostcodes(String prettyPostcode) {
        Postcodes postcode = new Postcodes();
        postcode.setPretty(prettyPostcode);
        return List.of(postcode);
    }
}
