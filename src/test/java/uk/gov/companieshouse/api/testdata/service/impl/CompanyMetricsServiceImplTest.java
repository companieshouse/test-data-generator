package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.RegisterItem;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

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
    void create() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        final String etag = "ETAG";
        when(randomService.getEtag()).thenReturn(etag);
        
        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);
        
        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);
        
        ArgumentCaptor<CompanyMetrics> metricsCaptor
                = ArgumentCaptor.forClass(CompanyMetrics.class);
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
    void delete() {
        CompanyMetrics metrics = new CompanyMetrics();
        Optional<CompanyMetrics> optionalMetric = Optional.of(metrics);
        when(repository.findById(COMPANY_NUMBER)).thenReturn(optionalMetric);

        assertTrue(this.metricsService.delete(COMPANY_NUMBER));
        verify(repository).delete(metrics);
    }

    @Test
    void deleteNoDataException() {
        Optional<CompanyMetrics> optionalMetric = Optional.empty();
        when(repository.findById(COMPANY_NUMBER)).thenReturn(optionalMetric);
        
        assertFalse(this.metricsService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    @Test
    void createWithRegisters() {
        var directorsText = "directors";
        var publicRegisterText = "public-register";
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        RegistersSpec registersSpec = new RegistersSpec();
        registersSpec.setRegisterType(directorsText);
        registersSpec.setRegisterMovedTo(publicRegisterText);
        spec.setRegisters(List.of(registersSpec));

        final String etag = "ETAG";
        when(randomService.getEtag()).thenReturn(etag);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);

        ArgumentCaptor<CompanyMetrics> metricsCaptor
                = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();

        assertNotNull(metrics);
        assertEquals(COMPANY_NUMBER, metrics.getId());
        assertEquals(etag, metrics.getEtag());

        assertNotNull(metrics.getRegisters());
        assertEquals(1, metrics.getRegisters().size());
        RegisterItem registerItem = metrics.getRegisters().get(directorsText);
        assertNotNull(registerItem);
        assertEquals(publicRegisterText, registerItem.getRegisterMovedTo());
        assertEquals(LocalDate.now(), registerItem.getMovedOn());
    }
}