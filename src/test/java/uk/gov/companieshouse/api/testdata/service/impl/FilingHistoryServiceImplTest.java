package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AssociatedFiling;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.repository.FilingHistoryRepository;
import uk.gov.companieshouse.api.testdata.service.BarcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class FilingHistoryServiceImplTest {
    private static final Long UNENCODED_ID = 2345678L;
    private static final String TEST_ID = "test_id";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String BARCODE = "BARCODE";
    private static final int ENTITY_ID_LENGTH = 9;
    private static final String ENTITY_ID_PREFIX = "8";

    @Mock
    private FilingHistoryRepository repository;
    
    @Mock
    private RandomService randomService;

    @Mock
    private BarcodeService barcodeService;

    @InjectMocks
    private FilingHistoryServiceImpl filingHistoryService;

    @Test
    void create() throws DataException, BarcodeServiceException {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        
        FilingHistory savedHistory = new FilingHistory();
        when(repository.save(Mockito.any())).thenReturn(savedHistory);
        
        FilingHistory returnedHistory = this.filingHistoryService.create(spec);

        assertEquals(returnedHistory, savedHistory);
        
        ArgumentCaptor<FilingHistory> filingHistoryCaptor = ArgumentCaptor.forClass(FilingHistory.class);
        verify(repository).save(filingHistoryCaptor.capture());
        FilingHistory filingHistory = filingHistoryCaptor.getValue();
        assertEquals(TEST_ID, filingHistory.getId());
        assertEquals(COMPANY_NUMBER, filingHistory.getCompanyNumber());
        assertNotNull(filingHistory.getLinks());
        assertEquals("incorporation", filingHistory.getCategory());
        assertEquals("incorporation-company", filingHistory.getDescription());
        assertNotNull(filingHistory.getDate());
        assertEquals("NEWINC", filingHistory.getType());
        assertEquals(Integer.valueOf(10), filingHistory.getPages());
        assertEquals(ENTITY_ID_PREFIX + UNENCODED_ID, filingHistory.getEntityId());
        assertEquals("Certificate of incorporation general company details & statements of; officers, capital & shareholdings, guarantee, compliance memorandum of association",
                filingHistory.getOriginalDescription());
        assertEquals(BARCODE, filingHistory.getBarcode());

        List<AssociatedFiling> associatedFilings = filingHistory.getAssociatedFilings();
        assertEquals(2, associatedFilings.size());
        AssociatedFiling incorporation = associatedFilings.get(0);
        assertEquals("incorporation", incorporation.getCategory());
        assertNotNull(incorporation.getDate());
        assertEquals("model-articles-adopted", incorporation.getDescription());
        assertEquals("MODEL ARTICLES", incorporation.getType());

        AssociatedFiling capital = associatedFilings.get(1);
        assertNotNull(capital.getActionDate());
        assertEquals("capital", capital.getCategory());
        assertNotNull(capital.getDate());
        assertEquals("statement-of-capital", capital.getDescription());
        assertNotNull(capital.getDescriptionValues());
        assertEquals("11/09/19 Statement of Capital;GBP 1", capital.getOriginalDescription());
        assertEquals("SH01", capital.getType());
    }

    @Test
    void createBarcodeServiceException() throws BarcodeServiceException{
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        final String exceptionMessage = "Barcode error";
        when(barcodeService.getBarcode()).thenThrow(new BarcodeServiceException(exceptionMessage));

        DataException exception = assertThrows(DataException.class, () ->
                this.filingHistoryService.create(spec)
        );
        assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    void createMongoException() throws BarcodeServiceException{
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getNumber(ENTITY_ID_LENGTH)).thenReturn(UNENCODED_ID);
        when(randomService.addSaltAndEncode(ENTITY_ID_PREFIX + UNENCODED_ID, 8)).thenReturn(TEST_ID);
        when(barcodeService.getBarcode()).thenReturn(BARCODE);
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.filingHistoryService.create(spec)
        );
        assertEquals("Failed to save filing history", exception.getMessage());
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
        assertEquals("Failed to delete filing history", exception.getMessage());
    }
}