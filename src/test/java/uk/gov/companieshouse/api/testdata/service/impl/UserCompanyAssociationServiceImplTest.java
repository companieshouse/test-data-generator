package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.accounts.associations.model.Association;
import uk.gov.companieshouse.api.accounts.associations.model.ResponseBodyPost;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.accountsassociation.PrivateAccountsAssociationResourceHandler;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationAddPost;
import uk.gov.companieshouse.api.handler.accountsassociation.request.PrivateAccountsAssociationSearchGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.UserCompanyAssociation;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.ApiClientService;

@ExtendWith(MockitoExtension.class)
class UserCompanyAssociationServiceImplTest {

    private static final String COMPANY_NUMBER = "TC123456";
    private static final String USER_ID = "userId";
    private static final String USER_EMAIL = "user@test.com";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String STATUS_CONFIRMED = "confirmed";
    private static final String STATUS_REMOVED = "REMOVED";
    private static final String ASSOCIATION_LINK_WITH_SLASH = "/associations/" + ASSOCIATION_ID;
    private static final String ASSOCIATION_LINK_NO_SLASH = ASSOCIATION_ID;

    @Mock
    private UserCompanyAssociationRepository repository;

    @Mock
    private ApiClientService apiClientService;

    @Mock
    private InternalApiClient internalApiClient;

    @Mock
    private PrivateAccountsAssociationResourceHandler resourceHandler;

    @Mock
    private PrivateAccountsAssociationAddPost associationPost;

    @Mock
    private PrivateAccountsAssociationSearchGet associationSearch;

    @Mock
    private ApiResponse<ResponseBodyPost> apiResponseCreate;

    @Mock
    private ApiResponse<Association> apiResponseSearch;

    @Mock
    private ResponseBodyPost associationResponse;

    @InjectMocks
    private UserCompanyAssociationServiceImpl service;

    @Test
    void create_ReturnsAssociationData_WhenLinkContainsSlash() throws Exception {
        UserCompanyAssociationSpec spec = new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId(USER_ID);

        mockApiClientChainForCreate();

        when(associationResponse.getAssociationLink()).thenReturn(ASSOCIATION_LINK_WITH_SLASH);

        UserCompanyAssociationData result = service.create(spec);

        assertNotNull(result);
        assertEquals(ASSOCIATION_ID, result.getId());
        assertEquals(ASSOCIATION_LINK_WITH_SLASH, result.getAssociationLink());
        verify(associationPost).execute();
    }

    @Test
    void create_ReturnsAssociationData_WhenLinkContainsNoSlash() throws Exception {
        UserCompanyAssociationSpec spec = new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId(USER_ID);

        mockApiClientChainForCreate();

        when(associationResponse.getAssociationLink()).thenReturn(ASSOCIATION_LINK_NO_SLASH);

        UserCompanyAssociationData result = service.create(spec);

        assertNotNull(result);
        assertEquals(ASSOCIATION_ID, result.getId());
        assertEquals("/associations/" + ASSOCIATION_ID, result.getAssociationLink());
    }

    @Test
    void create_ThrowsDataException_WhenApiReturnsError() throws Exception {
        UserCompanyAssociationSpec spec = new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId(USER_ID);

        when(apiClientService.getInternalApiClientForPrivateAccountApiUrl()).thenReturn(internalApiClient);
        when(internalApiClient.privateAccountsAssociationResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.addAssociation(anyString(), eq(COMPANY_NUMBER), eq(USER_ID))).thenReturn(associationPost);
        when(associationPost.execute()).thenThrow(mock(ApiErrorResponseException.class));

        DataException exception = assertThrows(DataException.class, () -> service.create(spec));
        assertTrue(exception.getMessage().contains("Error creating association"));
    }

    @Test
    void create_ThrowsDataException_WhenUriValidationErrorOccurs() throws Exception {
        UserCompanyAssociationSpec spec = new UserCompanyAssociationSpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setUserId(USER_ID);

        when(apiClientService.getInternalApiClientForPrivateAccountApiUrl()).thenReturn(internalApiClient);
        when(internalApiClient.privateAccountsAssociationResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.addAssociation(anyString(), eq(COMPANY_NUMBER), eq(USER_ID))).thenReturn(associationPost);
        when(associationPost.execute()).thenThrow(new URIValidationException("URI Error"));

        DataException exception = assertThrows(DataException.class, () -> service.create(spec));
        assertTrue(exception.getMessage().contains("Error creating association"));
    }

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

    @Test
    void searchAssociation_ReturnsAssociation_WhenFound() throws Exception {
        Association mockAssociation = new Association();
        mockAssociation.setId(ASSOCIATION_ID);

        mockApiClientChainForSearch();
        when(apiResponseSearch.getData()).thenReturn(mockAssociation);

        Association result = service.searchAssociation(COMPANY_NUMBER, USER_ID, USER_EMAIL);

        assertNotNull(result);
        assertEquals(ASSOCIATION_ID, result.getId());
    }

    @Test
    void searchAssociation_ReturnsNull_WhenNotFound() throws Exception {
        mockApiClientChainForSearch();
        when(apiResponseSearch.getData()).thenReturn(null);

        Association result = service.searchAssociation(COMPANY_NUMBER, USER_ID, USER_EMAIL);

        assertEquals(null, result);
    }

    @Test
    void searchAssociation_ThrowsDataException_WhenApiErrorOccurs() throws Exception {
        when(apiClientService.getInternalApiClientForPrivateAccountApiUrl()).thenReturn(internalApiClient);
        when(internalApiClient.privateAccountsAssociationResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.searchForAssociation(anyString(), eq(USER_ID), eq(USER_EMAIL), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(associationSearch);
        when(associationSearch.execute()).thenThrow(mock(ApiErrorResponseException.class));

        DataException exception = assertThrows(DataException.class, () ->
                service.searchAssociation(COMPANY_NUMBER, USER_ID, USER_EMAIL)
        );
        assertTrue(exception.getMessage().contains("Error searching for association"));
    }

    // --- Helpers ---

    private void mockApiClientChainForCreate() throws Exception {
        when(apiClientService.getInternalApiClientForPrivateAccountApiUrl()).thenReturn(internalApiClient);
        when(internalApiClient.privateAccountsAssociationResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.addAssociation(anyString(), eq(COMPANY_NUMBER), eq(USER_ID))).thenReturn(associationPost);
        when(associationPost.execute()).thenReturn(apiResponseCreate);
        when(apiResponseCreate.getData()).thenReturn(associationResponse);
    }

    private void mockApiClientChainForSearch() throws Exception {
        when(apiClientService.getInternalApiClientForPrivateAccountApiUrl()).thenReturn(internalApiClient);
        when(internalApiClient.privateAccountsAssociationResourceHandler()).thenReturn(resourceHandler);
        when(resourceHandler.searchForAssociation(
                "/associations/companies/" + COMPANY_NUMBER + "/search",
                USER_ID,
                USER_EMAIL,
                "confirmed", "awaiting-approval", "migrated", "unauthorised")
        ).thenReturn(associationSearch);

        when(associationSearch.execute()).thenReturn(apiResponseSearch);
    }
}