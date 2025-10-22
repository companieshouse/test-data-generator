package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.identityverification.model.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class IdentityVerificationServiceImplTest {

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;
    @Mock
    private UvidRepository uvidRepository;

    @InjectMocks
    private IdentityVerificationServiceImpl service;

    @Captor
    private ArgumentCaptor<Uvid> uvidCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new IdentityVerificationServiceImpl(internalApiClientSupplier, uvidRepository);
    }

    @Test
    void createUvidInMongo_shouldSaveUvidWithCorrectFields() {
        String identityId = "identity-123";
        String generatedUvid = "uvid-456";
        service.createUvidInMongo(identityId, generatedUvid);

        verify(uvidRepository, times(1)).save(uvidCaptor.capture());
        Uvid saved = uvidCaptor.getValue();
        assertNotNull(saved.getId());
        assertEquals(generatedUvid, saved.getUvid());
        assertEquals("permanent", saved.getType());
        assertEquals(identityId, saved.getIdentityId());
        assertNotNull(saved.getCreated());
        assertEquals("1.0", saved.getSchemaVersion());
    }
}