package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UvidLookupServiceTest {

    @Mock
    private UvidRepository uvidRepository;

    @InjectMocks
    private UvidLookupService uvidLookupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUvidByIdentityId_returnsUvid_whenEntityFound() {
        String identityId = "id123";
        String expectedUvid = "uvid-abc";
        Uvid uvidEntity = new Uvid();
        uvidEntity.setUvid(expectedUvid);

        when(uvidRepository.findByIdentityId(identityId)).thenReturn(uvidEntity);

        String result = uvidLookupService.getUvidByIdentityId(identityId);

        assertEquals(expectedUvid, result);
        verify(uvidRepository, times(1)).findByIdentityId(identityId);
    }

    @Test
    void getUvidByIdentityId_returnsNull_whenEntityNotFound() {
        String identityId = "id456";

        when(uvidRepository.findByIdentityId(identityId)).thenReturn(null);

        String result = uvidLookupService.getUvidByIdentityId(identityId);

        assertNull(result);
        verify(uvidRepository, times(1)).findByIdentityId(identityId);
    }
}