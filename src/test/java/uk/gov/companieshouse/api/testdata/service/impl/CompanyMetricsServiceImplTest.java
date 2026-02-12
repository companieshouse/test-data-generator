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
import java.util.Map;
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
import uk.gov.companieshouse.api.testdata.model.rest.OfficerRoles;
import uk.gov.companieshouse.api.testdata.model.rest.PscType;
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
        spec.setCompanyWithPopulatedStructureOnly(false);

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
        spec.setNumberOfPscs(2);
        spec.setPscType(List.of(PscType.INDIVIDUAL_BENEFICIAL_OWNER, PscType.CORPORATE_BENEFICIAL_OWNER));
        spec.setCompanyWithPopulatedStructureOnly(false);
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
        spec.setCompanyWithPopulatedStructureOnly(false);
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
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics returnedMetrics = this.metricsService.create(spec);

        assertEquals(savedMetrics, returnedMetrics);
        ArgumentCaptor<CompanyMetrics> metricsCaptor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(metricsCaptor.capture());
        CompanyMetrics metrics = metricsCaptor.getValue();
        assertEquals(0, metrics.getActivePscCount());
    }

    @Test
    void createWithSuperSecurePscs() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setHasSuperSecurePscs(true);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics result = metricsService.create(spec);

        assertEquals(savedMetrics, result);
        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(1, metrics.getActivePscCount());
    }

    @Test
    void createWithNumberOfPsc() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(5);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics result = metricsService.create(spec);

        assertEquals(savedMetrics, result);
        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(5, metrics.getActivePscCount());
    }

    @Test
    void createWithRegisters() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithPopulatedStructureOnly(false);
        RegistersSpec register = new RegistersSpec();
        register.setRegisterType("directors");
        register.setRegisterMovedTo("public-register");
        spec.setRegisters(List.of(register));

        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics result = metricsService.create(spec);

        assertEquals(savedMetrics, result);
        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        Map<String, RegisterItem> registers = metrics.getRegisters();
        assertEquals(1, registers.size());
        RegisterItem item = registers.get("directors");
        assertEquals("public-register", item.getRegisterMovedTo());
        assertEquals(LocalDate.now(), item.getMovedOn());
    }

    @Test
    void testSetActiveDirectorsCountAndRegisters() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(5);
        spec.setOfficerRoles(List.of(OfficerRoles.DIRECTOR));
        spec.setCompanyWithPopulatedStructureOnly(false);
        RegistersSpec register = new RegistersSpec();
        register.setRegisterType("directors");
        register.setRegisterMovedTo("public-register");
        spec.setRegisters(List.of(register));

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics savedMetrics = captor.getValue();

        assertEquals(5, savedMetrics.getActiveDirectorsCount());

        assertNotNull(savedMetrics.getRegisters());
        assertEquals(1, savedMetrics.getRegisters().size());
        RegisterItem item = savedMetrics.getRegisters().get("directors");
        assertNotNull(item);
        assertEquals("public-register", item.getRegisterMovedTo());
        assertEquals(LocalDate.now(), item.getMovedOn());
    }

    @Test
    void testSetActiveDirectorsCountWithoutDirectorRole() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfAppointments(5);
        spec.setOfficerRoles(List.of(OfficerRoles.CIC_MANAGER));
        spec.setCompanyWithPopulatedStructureOnly(false);
        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics savedMetrics = captor.getValue();

        assertEquals(1, savedMetrics.getActiveDirectorsCount());
    }

    @Test
    void deleteExistingCompanyMetrics() {
        CompanyMetrics metrics = new CompanyMetrics();
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.of(metrics));

        boolean result = metricsService.delete(COMPANY_NUMBER);

        assertTrue(result);
        verify(repository).delete(metrics);
    }

    @Test
    void deleteNonExistingCompanyMetrics() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());

        boolean result = metricsService.delete(COMPANY_NUMBER);

        assertFalse(result);
        verify(repository).findById(COMPANY_NUMBER);
    }

    @Test
    void createWithActivePscStatementsCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setActiveStatements(3);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(3, metrics.getActivePscStatementsCount());
    }

    @Test
    void createWithActivePscStatementsCountFromNumberOfPsc() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(2);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(2, metrics.getActivePscStatementsCount());
    }

    @Test
    void createWithDefaultActivePscStatementsCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(0, metrics.getActivePscStatementsCount());
    }

    @Test
    void createWithGivenWithdrawnPscStatementsCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setWithdrawnStatements(4);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(4, metrics.getWithdrawnStatementsCount());
    }

    @Test
    void createWithDefaultWithdrawnPscStatementsCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithPopulatedStructureOnly(false);
        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(0, metrics.getWithdrawnStatementsCount());
    }

    @Test
    void setPscCount_WhenPscActiveFalse_ShouldSetCorrectCount() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(3);
        spec.setPscActive(false);
        spec.setCompanyWithPopulatedStructureOnly(false);
        CompanyMetrics metrics = new CompanyMetrics();
        metricsService.setPscCount(metrics, spec);

        assertEquals(3, metrics.getPscCount());
    }

    @Test
    void setPscCount_WhenPscActiveTrue_ShouldSetCorrectCount() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(3);
        spec.setPscActive(true);
        spec.setCompanyWithPopulatedStructureOnly(false);

        CompanyMetrics metrics = new CompanyMetrics();
        metricsService.setPscCount(metrics, spec);

        assertEquals(3, metrics.getPscCount());
    }

    @Test
    void setPscCount_WhenPscActiveNull_ShouldSetCorrectCount() {
        CompanySpec spec = new CompanySpec();
        spec.setNumberOfPscs(3);
        spec.setPscActive(null);
        spec.setCompanyWithPopulatedStructureOnly(false);

        CompanyMetrics metrics = new CompanyMetrics();
        metricsService.setPscCount(metrics, spec);

        assertEquals(3, metrics.getPscCount());
    }

    @Test
    void setPscCount_WhenSuperSecurePscs_ShouldSetCountToOne() {
        CompanySpec spec = new CompanySpec();
        spec.setHasSuperSecurePscs(true);
        spec.setCompanyWithPopulatedStructureOnly(false);

        CompanyMetrics metrics = new CompanyMetrics();
        metricsService.setPscCount(metrics, spec);

        assertEquals(1, metrics.getPscCount());
    }

    @Test
    void setPscCount_WhenNoPscCount_ShouldSetCountToZero() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyWithPopulatedStructureOnly(false);
        CompanyMetrics metrics = new CompanyMetrics();
        metricsService.setPscCount(metrics, spec);

        assertEquals(0, metrics.getPscCount());
    }

    @Test
    void setPscActiveToFalse_ShouldSetCeasedPscCount() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(5);
        spec.setPscActive(false);
        spec.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(1, metrics.getCeasedPscCount());
    }

    @Test
    void create_WithPscActiveFalse_ShouldSetCorrectCounts() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(4);
        spec.setPscActive(false);
        spec.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(3, metrics.getActivePscCount());
        assertEquals(4, metrics.getPscCount());
    }

    @Test
    void create_WithPscActiveTrue_ShouldSetCorrectCounts() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(4);
        spec.setPscActive(true);
        spec.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(4, metrics.getActivePscCount());
        assertEquals(4, metrics.getPscCount());
    }

    @Test
    void create_WithPscActiveNull_ShouldSetCorrectCounts() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNumberOfPscs(4);
        spec.setPscActive(null);
        spec.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEtag()).thenReturn(ETAG);
        when(repository.save(any())).thenReturn(new CompanyMetrics());

        metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(4, metrics.getActivePscCount());
        assertEquals(4, metrics.getPscCount());
    }

    @Test
    void create_WithNoDefaultOfficerTrue_SetsActiveDirectorsCountToZero() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setNoDefaultOfficer(true);
        spec.setCompanyWithPopulatedStructureOnly(false);

        when(randomService.getEtag()).thenReturn(ETAG);
        CompanyMetrics savedMetrics = new CompanyMetrics();
        when(repository.save(any())).thenReturn(savedMetrics);

        CompanyMetrics result = metricsService.create(spec);

        ArgumentCaptor<CompanyMetrics> captor = ArgumentCaptor.forClass(CompanyMetrics.class);
        verify(repository).save(captor.capture());
        CompanyMetrics metrics = captor.getValue();

        assertEquals(0, metrics.getActiveDirectorsCount());
        assertEquals(savedMetrics, result);
    }

    @Test
    void createReturnsUnsavedMetricsWhenCompanyWithDataStructureIsTrue() {
        CompanySpec spec = new CompanySpec();
        spec.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyWithPopulatedStructureOnly(true);

        when(randomService.getEtag()).thenReturn(ETAG);

        CompanyMetrics result = metricsService.create(spec);

        assertNotNull(result);
        assertEquals(COMPANY_NUMBER, result.getId());
        assertEquals(ETAG, result.getEtag());
        verify(repository, never()).save(any());
    }

}