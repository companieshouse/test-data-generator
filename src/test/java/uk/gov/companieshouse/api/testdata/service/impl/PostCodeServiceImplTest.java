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
import uk.gov.companieshouse.api.testdata.repository.PostCodesRepository;

@ExtendWith(MockitoExtension.class)
public class PostCodeServiceImplTest {
    @Mock
    private PostCodesRepository postCodesRepository;

    @InjectMocks
    private PostcodeServiceImpl postCodeService;

    @Test
    void testGet_ReturnsPostCodes_WhenPostCodesExist() {
        String country = "England";
        Postcodes mockPostCode = mock(Postcodes.class);
        when(mockPostCode.getCountry()).thenReturn(country);
        List<Postcodes> mockPostCodes = List.of(mockPostCode);
        when(postCodesRepository.findByCountryContaining(country)).thenReturn(mockPostCodes);
        List<Postcodes> result = postCodeService.get(country);
        assertEquals(country, result.getFirst().getCountry());
        verify(postCodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenNoPostCodesExist() {
        String country = "Unknown";
        when(postCodesRepository.findByCountryContaining(country))
                .thenReturn(Collections.emptyList());
        List<Postcodes> result = postCodeService.get(country);
        assertEquals(Collections.emptyList(), result);
        verify(postCodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenCountryIsNull() {
        when(postCodesRepository.findByCountryContaining(null))
                .thenReturn(Collections.emptyList());
        List<Postcodes> result = postCodeService.get(null);
        assertEquals(Collections.emptyList(), result);
        verify(postCodesRepository, times(1)).findByCountryContaining(null);
    }
}
