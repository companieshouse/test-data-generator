package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.impl.FilingHistoryServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class FilingHistoryServiceImplTest {

    @Mock
    private ITestDataHelperService testDataHelperService;

    @Mock
    private FilingHistoryRepository filingHistoryRepository;

    private IFilingHistoryService IFilingHistoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.IFilingHistoryService = new FilingHistoryServiceImpl(testDataHelperService, filingHistoryRepository);
    }

    @Test
    void testCreateNoException() throws DataException {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        FilingHistory createdHistory = this.IFilingHistoryService.create("12345678");

        assertEquals("test_id", createdHistory.getId());
        assertEquals("12345678", createdHistory.getCompanyNumber());
        assertEquals(new Integer(1), createdHistory.getTotalCount());
        assertEquals(1, createdHistory.getFilingHistoryItems().size());
    }

    @Test
    void testCreateDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(filingHistoryRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.IFilingHistoryService.create("12345678");
        });
    }

    @Test
    void testCreateMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(filingHistoryRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.IFilingHistoryService.create("12345678");
        });
    }

    @Test
    void testDeleteMongoException() {
        when(filingHistoryRepository.findByCompanyNumber("12345678"))
                .thenReturn(new FilingHistory());
        doThrow(MongoException.class).when(filingHistoryRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.IFilingHistoryService.delete("12345678");
        });
    }
}