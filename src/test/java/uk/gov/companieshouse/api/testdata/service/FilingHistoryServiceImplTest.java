package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import uk.gov.companieshouse.api.testdata.repository.filinghistory.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.impl.FilingHistoryServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {

    @Mock
    private TestDataHelperService testDataHelperService;

    @Mock
    private FilingHistoryRepository filingHistoryRepository;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    @Test
    void createNoException() throws DataException {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        FilingHistory createdHistory = this.filingHistoryService.create("12345678");

        assertEquals("test_id", createdHistory.getId());
        assertEquals("12345678", createdHistory.getCompanyNumber());
        assertEquals(Integer.valueOf(1), createdHistory.getTotalCount());
        assertEquals(1, createdHistory.getFilingHistoryItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(filingHistoryRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () -> {
            this.filingHistoryService.create("12345678");
        });
    }

    @Test
    void createMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn("test_id");
        when(filingHistoryRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () -> {
            this.filingHistoryService.create("12345678");
        });
    }

    @Test
    void deleteMongoException() {
        when(filingHistoryRepository.findByCompanyNumber("12345678"))
                .thenReturn(new FilingHistory());
        doThrow(MongoException.class).when(filingHistoryRepository).delete(any());
        assertThrows(DataException.class, () -> {
            this.filingHistoryService.delete("12345678");
        });
    }
}