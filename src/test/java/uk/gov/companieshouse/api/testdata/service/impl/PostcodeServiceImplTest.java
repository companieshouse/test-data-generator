package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.repository.PostcodesRepository;

@ExtendWith(MockitoExtension.class)
class PostcodeServiceImplTest {
    @Mock
    private PostcodesRepository postcodesRepository;

    @InjectMocks
    private PostcodeServiceImpl postcodeService;

    @Test
    void testGetPostcodesForEngland() {
        // Arrange
        String country = "gb-eng";
        List<Postcodes> mockPostcodes = createMockPostcodes("E1 6AN");

        when(postcodesRepository.findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);

        // Act
        List<Postcodes> result = postcodeService.get(country);

        // Assert
        assertEquals(1, result.size());
        assertEquals("E1 6AN", result.getFirst().getPretty());
        verify(postcodesRepository, times(1)).findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForWales() {
        // Arrange
        String country = "gb-wls";
        List<Postcodes> mockPostcodes = createMockPostcodes("CF10 1AA");

        when(postcodesRepository.findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);

        // Act
        List<Postcodes> result = postcodeService.get(country);

        // Assert
        assertEquals(1, result.size());
        assertEquals("CF10 1AA", result.get(0).getPretty());
        verify(postcodesRepository, times(1)).findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForScotland() {
        // Arrange
        String country = "gb-sct";
        List<Postcodes> mockPostcodes = createMockPostcodes("EH1 1BB");

        when(postcodesRepository.findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        )).thenReturn(mockPostcodes);

        // Act
        List<Postcodes> result = postcodeService.get(country);

        // Assert
        assertEquals(1, result.size());
        assertEquals("EH1 1BB", result.get(0).getPretty());
        verify(postcodesRepository, times(1)).findByStrippedContaining(
                org.mockito.ArgumentMatchers.anyList(),
                org.mockito.ArgumentMatchers.any()
        );
    }

    @Test
    void testGetPostcodesForInvalidCountry() {
        // Arrange
        String invalidCountry = "invalid-country";

        // Act & Assert
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
