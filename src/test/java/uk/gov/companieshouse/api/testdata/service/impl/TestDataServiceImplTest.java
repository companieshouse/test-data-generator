package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String AUTH_CODE = "123456";
    private static final String OFFICER_ID = "OFFICER_ID";
    private static final String APPOINTMENT_ID = "APPOINTMENT_ID";
    private static final String SCOTTISH_COMPANY_PREFIX = "SC";
    private static final String NI_COMPANY_PREFIX = "NI";
    private static final String API_URL = "http://localhost:4001";

    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private DataService<FilingHistory> filingHistoryService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private DataService<Appointment> appointmentService;
    @Mock
    private DataService<CompanyMetrics> metricsService;
    @Mock
    private DataService<CompanyPscStatement> companyPscStatementService;
    @Mock
    private DataService<CompanyPscs> companyPscsService;

    @Mock
    private RandomService randomService;
    @InjectMocks
    private TestDataServiceImpl testDataService;
    
    @Captor
    private ArgumentCaptor<CompanySpec> specCaptor;

    @BeforeEach
    void setUp() {
        this.testDataService.setAPIUrl(API_URL);
    }

    @Test
    void createCompanyDataDefaultSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        final String fullCompanyNumber = COMPANY_NUMBER;
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(fullCompanyNumber, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataScottishSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(6)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        final String fullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(fullCompanyNumber, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.SCOTLAND, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(fullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + fullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataNISpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(6)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        final String fullCompanyNumber = NI_COMPANY_PREFIX + COMPANY_NUMBER;
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(fullCompanyNumber, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.NI, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(fullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + fullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataRegisteredEmailAddressChangeSpec() throws Exception {
        String baseCompanyNumber = "123456";
        final String companyNumber = "RE" + baseCompanyNumber;

        CompanySpec spec = new CompanySpec();
        spec.setRegisteredEmailAddressChange(true);
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(6)).thenReturn(Long.valueOf(baseCompanyNumber));
        when(companyProfileService.companyExists(companyNumber)).thenReturn(false);
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);

        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(companyNumber, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(companyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + companyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataNoRegisteredEmailAddressChangeSpec() throws Exception {
        final String companyNumber = "12345678";

        CompanySpec spec = new CompanySpec();
        spec.setRegisteredEmailAddressChange(false);
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(8)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(companyNumber)).thenReturn(false);
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);

        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(companyNumber, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(companyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + companyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataExistingNumber() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);
        
        final String existingCompanyNumber = "555555";
        final String fullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        when(this.randomService.getNumber(6)).thenReturn(Long.valueOf(existingCompanyNumber)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(SCOTTISH_COMPANY_PREFIX + existingCompanyNumber)).thenReturn(true);
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(fullCompanyNumber, expectedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);

        assertEquals(fullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + fullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }
    @Test
    void createCompanyDataRollBack() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        
        final String fullCompanyNumber = spec.getJurisdiction().getCompanyNumberPrefix() + COMPANY_NUMBER;
        when(this.randomService.getNumber(Mockito.anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        
        DataException pscStatementException = new DataException("error");
        when(companyPscStatementService.create(spec)).thenThrow(pscStatementException);

        DataException thrown = assertThrows(DataException.class, () -> this.testDataService.createCompanyData(spec));

        assertEquals(pscStatementException, thrown.getCause());
        
        verify(companyProfileService).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(fullCompanyNumber, expectedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), expectedSpec.getJurisdiction());

        verify(filingHistoryService).create(expectedSpec);
        verify(companyAuthCodeService).create(expectedSpec);
        verify(appointmentService).create(expectedSpec);
        verify(metricsService).create(expectedSpec);

        // Verify we roll back data
        verify(companyProfileService).delete(fullCompanyNumber);
        verify(filingHistoryService).delete(fullCompanyNumber);
        verify(companyAuthCodeService).delete(fullCompanyNumber);
        verify(appointmentService).delete(fullCompanyNumber);
        verify(companyPscStatementService).delete(fullCompanyNumber);
        verify(metricsService).delete(fullCompanyNumber);
        verify(companyPscsService,times(1)).delete(fullCompanyNumber);
    }

    @Test
    void deleteCompanyData() throws Exception {
        this.testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void createCompanyDataNullSpec() throws DataException {
        CompanySpec spec = null;
        assertThrows(IllegalArgumentException.class, () -> this.testDataService.createCompanyData(spec));
        
        verify(companyProfileService, never()).delete(any());
        verify(filingHistoryService, never()).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, never()).delete(COMPANY_NUMBER);
        verify(appointmentService, never()).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, never()).delete(COMPANY_NUMBER);
        verify(companyPscsService, never()).delete(COMPANY_NUMBER);
        verify(metricsService, never()).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataProfileException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataFilingHistoryException() {
        RuntimeException ex = new RuntimeException("exception");
        when(filingHistoryService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataAuthCodeException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataAppointmentException() {
        RuntimeException ex = new RuntimeException("exception");
        when(appointmentService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataPscStatementException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataPscsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);

    }

    @Test
    void deleteCompanyDataMetricsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(metricsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataMultipleExceptions() {
        RuntimeException profileException = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(profileException);

        RuntimeException authCodeException = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(authCodeException);

        RuntimeException pscStatementException = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(pscStatementException);

        DataException thrown = assertThrows(DataException.class,
                () -> this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(3, thrown.getSuppressed().length);
        assertEquals(profileException, thrown.getSuppressed()[0]);
        assertEquals(authCodeException, thrown.getSuppressed()[1]);
        assertEquals(pscStatementException, thrown.getSuppressed()[2]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
    }
}