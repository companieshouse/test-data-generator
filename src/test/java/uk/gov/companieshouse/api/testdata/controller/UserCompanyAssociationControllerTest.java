package uk.gov.companieshouse.api.testdata.controller;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.service.UserCompanyAssociationService;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCompanyAssociationControllerTest {

    private static final String COMPANY_NUMBER = "TC123456";
    private static final String AUTH_CODE_APPROVAL_ROUTE = "auth_code";
    private static final String CONFIRMED_STATUS = "confirmed";
    private static final String USER_ID = "userId";
    private static final String ASSOCIATION_ID = "associationId";

    @Mock
    private UserCompanyAssociationService userCompanyAssociationService;

    @InjectMocks
    private UserCompanyAssociationController userCompanyAssociationController;

    @Test
    void createUserCompanyAssociation() throws Exception {
        UserCompanyAssociationRequest spec = new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setStatus(CONFIRMED_STATUS);
        spec.setApprovalRoute(AUTH_CODE_APPROVAL_ROUTE);

        UserCompanyAssociationResponse association = new UserCompanyAssociationResponse(
                new ObjectId(), COMPANY_NUMBER, USER_ID, null,
                CONFIRMED_STATUS, AUTH_CODE_APPROVAL_ROUTE, null);

        when(userCompanyAssociationService.create(spec)).thenReturn(association);
        ResponseEntity<UserCompanyAssociationResponse> response =
                userCompanyAssociationController.createAssociation(spec);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(COMPANY_NUMBER, response.getBody().getCompanyNumber());
        assertEquals(USER_ID, response.getBody().getUserId());
    }

    @Test
    void createUserCompanyAssociationException() throws Exception {
        UserCompanyAssociationRequest spec = new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(userCompanyAssociationService.create(spec)).thenThrow(new RuntimeException("boom"));

        DataException thrown = assertThrows(DataException.class, () ->
                userCompanyAssociationController.createAssociation(spec));
        assertEquals("Error creating the association", thrown.getMessage());
    }

    @Test
    void deleteUserCompanyAssociation() throws Exception {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response =
                userCompanyAssociationController.deleteAssociation(ASSOCIATION_ID);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userCompanyAssociationService).delete(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationNotFound() throws Exception {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID)).thenReturn(false);
        ResponseEntity<Map<String, Object>> response =
                userCompanyAssociationController.deleteAssociation(ASSOCIATION_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(ASSOCIATION_ID, Objects.requireNonNull(response.getBody()).get("association_id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));
    }

    @Test
    void deleteUserCompanyAssociationException() {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenThrow(new RuntimeException("boom"));

        DataException thrown = assertThrows(DataException.class,
                () -> userCompanyAssociationController.deleteAssociation(ASSOCIATION_ID));
        assertEquals("Error deleting association", thrown.getMessage());
    }
}
