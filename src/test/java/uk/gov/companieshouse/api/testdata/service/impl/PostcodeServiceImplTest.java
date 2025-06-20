package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    void testGet_ReturnsPostCodes_WhenPostCodesExist() {
        String country = "England";
        Postcodes mockPostCode = mock(Postcodes.class);
        when(mockPostCode.getCountry()).thenReturn(country);
        List<Postcodes> mockPostCodes = List.of(mockPostCode);
        when(postcodesRepository.findByCountryContaining(country)).thenReturn(mockPostCodes);
        List<Postcodes> result = postcodeService.get(country);
        assertEquals(country, result.getFirst().getCountry());
        verify(postcodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenNoPostCodesExist() {
        String country = "Unknown";
        when(postcodesRepository.findByCountryContaining(country))
                .thenReturn(Collections.emptyList());
        List<Postcodes> result = postcodeService.get(country);
        assertEquals(Collections.emptyList(), result);
        verify(postcodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenCountryIsNull() {
        when(postcodesRepository.findByCountryContaining(null))
                .thenReturn(Collections.emptyList());
        List<Postcodes> result = postcodeService.get(null);
        assertEquals(Collections.emptyList(), result);
        verify(postcodesRepository, times(1)).findByCountryContaining(null);
    }
}
