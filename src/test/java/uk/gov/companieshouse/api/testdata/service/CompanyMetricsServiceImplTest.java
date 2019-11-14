package uk.gov.companieshouse.api.testdata.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyMetricsServiceImpl;

@ExtendWith(MockitoExtension.class)
class CompanyMetricsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";

    @Mock
    private RandomService randomService;
    
    @Mock
    private CompanyMetricsRepository repository;
    
    @InjectMocks
    private CompanyMetricsServiceImpl metricsService;

    @Test
    void create() throws DataException {
        final String etag = "ETAG";
        when(randomService.getEtag()).thenReturn(etag);
        
        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);
        
        CompanyMetrics returnedMetrics = this.metricsService.create(COMPANY_NUMBER);

        assertEquals(savedMetrics, returnedMetrics);
        
        ArgumentCaptor<CompanyMetrics> metricsCaptor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();

        assertNotNull(metrics);
        assertEquals(COMPANY_NUMBER, metrics.getId());
        assertEquals(etag, metrics.getEtag());
        
        assertEquals(1, metrics.getActivePscStatementsCount());
        assertEquals(0, metrics.getWithdrawnStatementsCount());
        assertEquals(1, metrics.getPscStatementsCount());
        
        assertEquals(0, metrics.getActivePscCount());
        assertEquals(0, metrics.getCeasedPscCount());
        assertEquals(0, metrics.getPscCount());
        
        assertEquals(1, metrics.getPscTotalCount());
        
        assertEquals(1, metrics.getActiveDirectorsCount());
        assertEquals(0, metrics.getActiveSecretariesCount());
        assertEquals(0, metrics.getActiveLlpMembersCount());
        assertEquals(0, metrics.getResignedOfficerCount());
        
        assertEquals(1, metrics.getActiveOfficersCount());
        assertEquals(1, metrics.getOfficersTotalCount());
    }

    @Test
    void createDuplicateKeyException() {
        when(repository.save(any())).thenThrow(DuplicateKeyException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.metricsService.create(COMPANY_NUMBER)
        );
        assertEquals("duplicate key", exception.getMessage());
    }

    @Test
    void createMongoExceptionException() {
        when(repository.save(any())).thenThrow(MongoException.class);

        DataException exception = assertThrows(DataException.class, () ->
            this.metricsService.create(COMPANY_NUMBER)
        );
        assertEquals("failed to insert", exception.getMessage());
    }

}