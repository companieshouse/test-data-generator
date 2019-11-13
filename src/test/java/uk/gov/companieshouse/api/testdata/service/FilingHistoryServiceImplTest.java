package uk.gov.companieshouse.api.testdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistoryItem;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.impl.FilingHistoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {

    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private FilingHistoryRepository repository;
    
    @Mock
    private RandomService randomService;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    @Test
    void create() throws DataException {
        final String transactionId = "TRANSACTION_ID";
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(randomService.getString(18)).thenReturn(transactionId);
        
        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);
        
        FilingHistory returnedHistory = this.filingHistoryService.create(COMPANY_NUMBER);

        assertEquals(returnedHistory, savedHistory);
        
        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());
        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertEquals(Integer.valueOf(1), filingHistory.getTotalCount());
        assertEquals(1, filingHistory.getFilingHistoryItems().size());
        
        FilingHistoryItem item = filingHistory.getFilingHistoryItems().get(0);
        assertEquals("NEWINC", item.getType());
        assertEquals("incorporation", item.getCategory());
        assertEquals("incorporation-company", item.getDescription());
        assertEquals(transactionId, item.getTransactionId());
        assertNotNull(item.getDate());
        assertEquals("/company/"+ COMPANY_NUMBER + "/filing-history/" + TEST_ID, item.getLinks().getSelf());
    }

    @Test
    void createDuplicateKeyException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.filingHistoryService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(randomService.getEncodedIdWithSalt(10, 8)).thenReturn(TEST_ID);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.filingHistoryService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
    }

    @Test
    void delete() throws Exception {
        FilingHistory filingHistory = new FilingHistory();

        when(repository.findByCompanyNumber(COMPANY_NUMBER)).thenReturn(filingHistory);

        filingHistoryService.delete(COMPANY_NUMBER);

        verify(repository).delete(filingHistory);
    }

    @Test
    void deleteNoCompany() {
        FilingHistory filingHistory = null;
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(filingHistory);
        NoDataFoundException exception = assertThrows(NoDataFoundException.class, () ->
            this.filingHistoryService.delete(COMPANY_NUMBER)
        );
        assertEquals("filing history data not found", exception.getMessage());
    }

    @Test
    void deleteMongoException() {
        FilingHistory filingHistory = new FilingHistory();
        when(repository.findByCompanyNumber(COMPANY_NUMBER))
                .thenReturn(filingHistory);
        doThrow(MongoException.class).when(repository).delete(filingHistory);
        DataException exception = assertThrows(DataException.class, () ->
            this.filingHistoryService.delete(COMPANY_NUMBER)
        );
        assertEquals("failed to delete", exception.getMessage());
    }
}