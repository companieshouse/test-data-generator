package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Backlog;
import uk.gov.companieshouse.api.testdata.repository.BacklogRepository;

@ExtendWith(MockitoExtension.class)
class BacklogServiceImplTest {

    private static final String BACKLOG_ID = "backlog-id";
    private static final String CASE_ID = "test-success123";

    @Mock
    private BacklogRepository backlogRepository;

    @InjectMocks
    private BacklogServiceImpl service;

    @Test
    void testUpdateBacklogCaseIdSuccess()
            throws NoDataFoundException, DataException {

        Backlog backlog = createBacklog();

        when(backlogRepository.findById(BACKLOG_ID))
                .thenReturn(Optional.of(backlog));

        service.updateBacklogCaseId(BACKLOG_ID, CASE_ID);

        assertEquals(CASE_ID, backlog.getCaseId());

        verify(backlogRepository, times(1))
                .findById(BACKLOG_ID);
        verify(backlogRepository, times(1))
                .save(backlog);
    }

    @Test
    void testUpdateBacklogCaseIdNotFound() {

        when(backlogRepository.findById(BACKLOG_ID))
                .thenReturn(Optional.empty());

        NoDataFoundException exception =
                assertThrows(NoDataFoundException.class,
                        () -> service.updateBacklogCaseId(
                                BACKLOG_ID, CASE_ID));

        assertEquals("backlog not found", exception.getMessage());

        verify(backlogRepository, times(1))
                .findById(BACKLOG_ID);
    }

    @Test
    void testUpdateBacklogCaseIdErrorOnSave() {

        Backlog backlog = createBacklog();

        when(backlogRepository.findById(BACKLOG_ID))
                .thenReturn(Optional.of(backlog));

        doThrow(ConstraintViolationException.class)
                .when(backlogRepository)
                .save(backlog);

        DataException exception =
                assertThrows(DataException.class,
                        () -> service.updateBacklogCaseId(
                                BACKLOG_ID, CASE_ID));

        verify(backlogRepository, times(1))
                .findById(BACKLOG_ID);
        verify(backlogRepository, times(1))
                .save(backlog);

        assertEquals("failed to update backlog", exception.getMessage());
    }

    private Backlog createBacklog() {
        Backlog backlog = new Backlog();
        backlog.setId(BACKLOG_ID);
        backlog.setCaseId("old-case-id");
        backlog.setCreated(Instant.now());
        return backlog;
    }
}