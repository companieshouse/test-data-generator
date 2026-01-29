package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.ApiClientService;

@ExtendWith(MockitoExtension.class)
class UserCompanyAssociationServiceImplTest {
    private static final String COMPANY_NUMBER = "TC123456";
    private static final String USER_ID = "userId";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String STATUS_CONFIRMED = "confirmed";
    private static final String STATUS_REMOVED = "REMOVED";

    @Mock
    private UserCompanyAssociationRepository repository;

    @Mock
    private ApiClientService apiClientService;

    @InjectMocks
    private UserCompanyAssociationServiceImpl service;

    @Test
    void deleteAssociation_UpdatesStatusToRemoved_WhenAssociationExists() {
        UserCompanyAssociation association = new UserCompanyAssociation();
        association.setId(ASSOCIATION_ID);
        association.setStatus(STATUS_CONFIRMED);
        association.setCompanyNumber(COMPANY_NUMBER);
        association.setUserId(USER_ID);
        when(repository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));

        boolean result = service.delete(ASSOCIATION_ID);

        assertTrue(result);
        assertEquals(STATUS_REMOVED, association.getStatus());
        verify(repository, times(1)).save(association);
    }

    @Test
    void deleteAssociation_ReturnsFalse_WhenAssociationDoesNotExist() {
        when(repository.findById(ASSOCIATION_ID)).thenReturn(Optional.empty());

        boolean result = service.delete(ASSOCIATION_ID);

        assertFalse(result);
        verify(repository, never()).save(any());
    }

    @Test
    void deleteAssociation_DoesNotChangeOtherFields() {
        UserCompanyAssociation association = new UserCompanyAssociation();
        association.setId(ASSOCIATION_ID);
        association.setStatus(STATUS_CONFIRMED);
        association.setCompanyNumber(COMPANY_NUMBER);
        association.setUserId(USER_ID);
        association.setEtag("etag123");
        when(repository.findById(ASSOCIATION_ID)).thenReturn(Optional.of(association));

        service.delete(ASSOCIATION_ID);

        assertEquals(COMPANY_NUMBER, association.getCompanyNumber());
        assertEquals(USER_ID, association.getUserId());
        assertEquals("etag123", association.getEtag());
    }
}