package uk.gov.companieshouse.api.testdata.service;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.impl.FilingHistoryServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private TestDataHelperService testDataHelperService;

    @Mock
    private FilingHistoryRepository filingHistoryRepository;
    
    @Mock
    private RandomService randomService;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    @Test
    void createNoException() throws DataException {
        when(testDataHelperService.getNewId()).thenReturn(TEST_ID);
        FilingHistory createdHistory = this.filingHistoryService.create(COMPANY_NUMBER);

        assertEquals(TEST_ID, createdHistory.getId());
        assertEquals(COMPANY_NUMBER, createdHistory.getCompanyNumber());
        assertEquals(Integer.valueOf(1), createdHistory.getTotalCount());
        assertEquals(1, createdHistory.getFilingHistoryItems().size());
    }

    @Test
    void createDuplicateKeyException() {
        when(testDataHelperService.getNewId()).thenReturn(TEST_ID);
        when(filingHistoryRepository.save(any())).thenThrow(DuplicateKeyException.class);

        assertThrows(DataException.class, () ->
            this.filingHistoryService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void createMongoExceptionException() {
        when(testDataHelperService.getNewId()).thenReturn(TEST_ID);
        when(filingHistoryRepository.save(any())).thenThrow(MongoException.class);

        assertThrows(DataException.class, () ->
            this.filingHistoryService.create(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteNoCompany() {
        when(filingHistoryRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(null);
        assertThrows(NoDataFoundException.class, () ->
            this.filingHistoryService.delete(COMPANY_NUMBER)
        );
    }

    @Test
    void deleteMongoException() {
        when(filingHistoryRepository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(new FilingHistory());
        doThrow(MongoException.class).when(filingHistoryRepository).delete(any());
        assertThrows(DataException.class, () ->
            this.filingHistoryService.delete(COMPANY_NUMBER)
        );
    }
}