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
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.repository.CompanyMetricsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class CompanyMetricsServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String ETAG = "etag";

    @Mock
    private RandomService randomService;

    @Mock
    private CompanyMetricsRepository repository;

    @InjectMocks
    private CompanyMetricsServiceImpl metricsService;

    @Test
    void create() {
        var directorsText = "directors";
        var publicRegisterText = "public-register";
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        RegistersSpec registersSpec = new RegistersSpec();
        registersSpec.setRegisterType(directorsText);
        registersSpec.setRegisterMovedTo(publicRegisterText);
        spec.setRegisters(List.of(registersSpec));

        when(randomService.getEtag()).thenReturn(ETAG);

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
        assertEquals(ETAG, metrics.getEtag());

        assertNotNull(metrics.getRegisters());
        assertEquals(1, metrics.getRegisters().size());
        RegisterItem registerItem = metrics.getRegisters().get(directorsText);
        assertNotNull(registerItem);
        assertEquals(publicRegisterText, registerItem.getRegisterMovedTo());
        assertEquals(LocalDate.now(), registerItem.getMovedOn());
    }

    @Test
    void delete() {
        CompanyMetrics companyMetrics = new CompanyMetrics();
        when(repository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.of(companyMetrics));

        assertTrue(this.metricsService.delete(COMPANY_NUMBER));
        verify(repository).delete(companyMetrics);
    }

    @Test
    void deleteNoDataException() {
        when(repository.findById(COMPANY_NUMBER))
                .thenReturn(Optional.empty());

        assertFalse(this.metricsService.delete(COMPANY_NUMBER));
        verify(repository, never()).delete(any());
    }

    @Test
    void createWhenCompanyTypeIsRegisteredOverseasEntity() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyType(CompanyType.REGISTERED_OVERSEAS_ENTITY);

        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);
        ArgumentCaptor<CompanyMetrics> metricsCaptor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();
        assertEquals(2, metrics.getActivePscCount());
    }

    @Test
    void createWhenHasSuperSecurePscsIsTrue() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setHasSuperSecurePscs(Boolean.TRUE);

        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);
        ArgumentCaptor<CompanyMetrics> metricsCaptor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();
        assertEquals(1, metrics.getActivePscCount());
    }

    @Test
    void createWithDefaultActivePscCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);
        ArgumentCaptor<CompanyMetrics> metricsCaptor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();
        assertEquals(3, metrics.getActivePscCount());
    }
}