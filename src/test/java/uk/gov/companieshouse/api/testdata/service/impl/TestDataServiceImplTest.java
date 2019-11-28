package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.OfficerAppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String AUTH_CODE = "123456";
    private static final String OFFICER_ID = "OFFICER_ID";
    private static final String APPOINTMENT_ID = "APPOINTMENT_ID";
    private static final String SCOTTISH_COMPANY_PREFIX = "SC";
    private static final String NI_COMPANY_PREFIX = "NI";

    @Mock
    private DataService<CompanyProfile> companyProfileService;
    @Mock
    private DataService<FilingHistory> filingHistoryService;
    @Mock
    private OfficerAppointmentService officerListService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private DataService<Appointment> appointmentService;
    @Mock
    private DataService<CompanyMetrics> metricsService;
    @Mock
    private DataService<CompanyPscStatement> companyPscStatementService;
    @Mock
    private RandomService randomService;
    @InjectMocks
    private TestDataServiceImpl testDataService;
    
    @Captor
    private ArgumentCaptor<CompanySpec> specCaptor;

    @Test
    void createCompanyDataDefaultSpec() throws DataException {
        CompanySpec spec = new CompanySpec();
        CompanyProfile mockCompany = new CompanyProfile();
        mockCompany.setCompanyNumber(COMPANY_NUMBER);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);

        when(this.randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(COMPANY_NUMBER, spec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, spec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(officerListService, times(1)).create(expectedSpec, OFFICER_ID, APPOINTMENT_ID);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);

        assertEquals(COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals("/company/" + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataScottishSpec() throws DataException {
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
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER, spec.getCompanyNumber());
        assertEquals(Jurisdiction.SCOTLAND, spec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(officerListService, times(1)).create(expectedSpec, OFFICER_ID, APPOINTMENT_ID);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);

        assertEquals(SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals("/company/" + SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataNISpec() throws DataException {
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
        when(this.companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(this.appointmentService.create(any())).thenReturn(mockAppointment);
        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(NI_COMPANY_PREFIX + COMPANY_NUMBER, spec.getCompanyNumber());
        assertEquals(Jurisdiction.NI, spec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(officerListService, times(1)).create(expectedSpec, OFFICER_ID, APPOINTMENT_ID);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);

        assertEquals(NI_COMPANY_PREFIX + COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals("/company/" + NI_COMPANY_PREFIX + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void deleteCompanyData() throws NoDataFoundException, DataException {
        this.testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(officerListService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
    }
}