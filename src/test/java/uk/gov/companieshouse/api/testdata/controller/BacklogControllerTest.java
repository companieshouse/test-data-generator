package uk.gov.companieshouse.api.testdata.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.BacklogRequest;
import uk.gov.companieshouse.api.testdata.service.BacklogService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BacklogControllerTest {

    @Mock
    private BacklogService backlogService;

    @InjectMocks
    private BacklogController backlogController;

    @Test
    void updateBacklogCaseIdSuccess()
            throws NoDataFoundException, DataException {

        final String backlogId = "backlog-id";
        final String caseId = "test-success123";

        BacklogRequest request = new BacklogRequest();
        request.setCaseId(caseId);

        ResponseEntity<Void> response =
                backlogController.updateBacklogCaseId(backlogId, request);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(backlogService).updateBacklogCaseId(backlogId, caseId);
    }

    @Test
    void updateBacklogCaseIdNoDataFoundException()
            throws NoDataFoundException, DataException {

        final String backlogId = "backlog-id";
        final String caseId = "test-success123";

        BacklogRequest request = new BacklogRequest();
        request.setCaseId(caseId);

        doThrow(new NoDataFoundException("backlog not found"))
                .when(backlogService)
                .updateBacklogCaseId(backlogId, caseId);

        assertThrows(
                NoDataFoundException.class,
                () -> backlogController.updateBacklogCaseId(backlogId, request));

        verify(backlogService).updateBacklogCaseId(backlogId, caseId);
    }

    @Test
    void updateBacklogCaseIdDataException()
            throws NoDataFoundException, DataException {

        final String backlogId = "backlog-id";
        final String caseId = "test-success123";

        BacklogRequest request = new BacklogRequest();
        request.setCaseId(caseId);

        doThrow(new DataException("failed to update backlog"))
                .when(backlogService)
                .updateBacklogCaseId(backlogId, caseId);

        assertThrows(
                DataException.class,
                () -> backlogController.updateBacklogCaseId(backlogId, request));

        verify(backlogService).updateBacklogCaseId(backlogId, caseId);
    }
}