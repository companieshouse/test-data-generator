package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Objects;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.service.AcspWorkflowService;

@ExtendWith(MockitoExtension.class)
class AcspMemberControllerTest {

    @Mock
    private AcspWorkflowService acspWorkflowService;

    @InjectMocks
    private AcspMemberController acspMemberController;

    @Test
    void createAcspMember() throws Exception {
        AcspMembersRequest request = new AcspMembersRequest();
        request.setUserId("rsf3pdwywvse5yz55mfodfx8");
        request.setUserRole("role");
        request.setStatus("active");
        request.setAcspProfile(new AcspProfileRequest());

        AcspMembersResponse acspMember = new AcspMembersResponse(
                new ObjectId(), "acspNumber", "userId", "active", "role");

        when(this.acspWorkflowService.createAcspMembersData(request)).thenReturn(acspMember);
        ResponseEntity<AcspMembersResponse> response
                = this.acspMemberController.createAcspMember(request);

        assertEquals(acspMember, response.getBody());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void createAcspMemberException() throws Exception {
        AcspMembersRequest request = new AcspMembersRequest();
        Throwable exception = new DataException("Error message");

        when(this.acspWorkflowService.createAcspMembersData(request)).thenThrow(exception);

        DataException thrown = assertThrows(DataException.class, () ->
                this.acspMemberController.createAcspMember(request));
        assertEquals(exception, thrown);
    }

    @Test
    void deleteAcspMember() throws Exception {
        final String acspMemberId = "memberId";

        when(this.acspWorkflowService.deleteAcspMembersData(acspMemberId)).thenReturn(true);
        ResponseEntity<Map<String, Object>> response
                = this.acspMemberController.deleteAcspMember(acspMemberId);

        assertNull(response.getBody());
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(acspWorkflowService).deleteAcspMembersData(acspMemberId);
    }

    @Test
    void deleteAcspMemberNotFound() throws Exception {
        final String acspMemberId = "memberId";

        when(this.acspWorkflowService.deleteAcspMembersData(acspMemberId)).thenReturn(false);
        ResponseEntity<Map<String, Object>> response
                = this.acspMemberController.deleteAcspMember(acspMemberId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("memberId", Objects.requireNonNull(response.getBody()).get("acsp-member-id"));
        assertEquals(HttpStatus.NOT_FOUND, response.getBody().get("status"));

        verify(acspWorkflowService).deleteAcspMembersData(acspMemberId);
    }

    @Test
    void deleteAcspMemberException() throws Exception {
        final String acspMemberId = "memberId";
        Throwable exception = new DataException("Error message");

        when(this.acspWorkflowService.deleteAcspMembersData(acspMemberId)).thenThrow(exception);

        DataException thrown = assertThrows(
                DataException.class, () -> this.acspMemberController.deleteAcspMember(acspMemberId));
        assertEquals(exception, thrown);
    }
}
