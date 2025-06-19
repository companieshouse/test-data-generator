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
import uk.gov.companieshouse.api.testdata.model.entity.PostCodes;
import uk.gov.companieshouse.api.testdata.repository.PostCodesRepository;

@ExtendWith(MockitoExtension.class)
public class PostCodeServiceImplTest {
    @Mock
    private PostCodesRepository postCodesRepository;

    @InjectMocks
    private PostCodeServiceImpl postCodeService;

    @Test
    void testGet_ReturnsPostCodes_WhenPostCodesExist() {
        String country = "England";
        PostCodes mockPostCode = mock(PostCodes.class);
        when(mockPostCode.getCountry()).thenReturn(country);
        List<PostCodes> mockPostCodes = List.of(mockPostCode);
        when(postCodesRepository.findByCountryContaining(country)).thenReturn(mockPostCodes);
        List<PostCodes> result = postCodeService.get(country);
        assertEquals(country, result.getFirst().getCountry());
        verify(postCodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenNoPostCodesExist() {
        String country = "Unknown";
        when(postCodesRepository.findByCountryContaining(country))
                .thenReturn(Collections.emptyList());
        List<PostCodes> result = postCodeService.get(country);
        assertEquals(Collections.emptyList(), result);
        verify(postCodesRepository, times(1)).findByCountryContaining(country);
    }

    @Test
    void testGet_ReturnsEmptyList_WhenCountryIsNull() {
        when(postCodesRepository.findByCountryContaining(null))
                .thenReturn(Collections.emptyList());
        List<PostCodes> result = postCodeService.get(null);
        assertEquals(Collections.emptyList(), result);
        verify(postCodesRepository, times(1)).findByCountryContaining(null);
    }
}
