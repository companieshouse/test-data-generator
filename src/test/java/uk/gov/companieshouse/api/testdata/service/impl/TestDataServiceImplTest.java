package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import uk.gov.companieshouse.api.testdata.model.entity.*;

import uk.gov.companieshouse.api.testdata.model.rest.*;

import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;

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
    private DataService<FilingHistory, CompanySpec> filingHistoryService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private DataService<Appointment, CompanySpec> appointmentService;
    @Mock
    private DataService<CompanyMetrics, CompanySpec> metricsService;
    @Mock
    private DataService<CompanyPscStatement, CompanySpec> companyPscStatementService;
    @Mock
    private DataService<CompanyPscs, CompanySpec> companyPscsService;
    @Mock
    private RandomService randomService;
    @Mock
    private UserService userService;
    @Mock
    private DataService<RoleData, RoleSpec> roleService;
    @Mock
    private DataService<AcspMembersData, AcspMembersSpec> acspMembersService;
    @InjectMocks
    private TestDataServiceImpl testDataService;
    @Mock
    private AcspMembersRepository acspMembersRepository;
    @Mock
    private DataService<AcspProfileData, AcspProfileSpec> acspProfileService;

    @Captor
    private ArgumentCaptor<CompanySpec> specCaptor;

    @Mock
    private DataService<IdentityData, IdentitySpec> identityService;

    @Mock
    private CompanyAuthAllowListService companyAuthAllowListService;

    @Mock
    private DataService<CompanyRegisters, CompanySpec> companyRegistersService;

    @BeforeEach
    void setUp() {
        this.testDataService.setAPIUrl(API_URL);
    }

    @Test
    void createCompanyDataDefaultSpec() throws Exception {
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

        CompanySpec spec = new CompanySpec();
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
    void createCompanyDataSpec() throws Exception {
        final String companyNumber = "12345678";

        CompanySpec spec = new CompanySpec();
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
        when(this.randomService.getNumber(6))
                .thenReturn(Long.valueOf(existingCompanyNumber))
                .thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(SCOTTISH_COMPANY_PREFIX + existingCompanyNumber))
                .thenReturn(true);
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
    void createCompanyDataWithCompanyRegisters() throws Exception {
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

        CompanySpec spec = new CompanySpec();
        RegistersSpec directorsRegister = new RegistersSpec();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));

        CompanyData createdCompany = this.testDataService.createCompanyData(spec);

        verify(companyProfileService, times(1)).create(specCaptor.capture());
        CompanySpec expectedSpec = specCaptor.getValue();
        assertEquals(COMPANY_NUMBER, expectedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, expectedSpec.getJurisdiction());

        verify(filingHistoryService, times(1)).create(expectedSpec);
        verify(companyAuthCodeService, times(1)).create(expectedSpec);
        verify(appointmentService, times(1)).create(expectedSpec);
        verify(companyPscStatementService, times(1)).create(expectedSpec);
        verify(metricsService, times(1)).create(expectedSpec);
        verify(companyPscsService, times(3)).create(expectedSpec);
        verify(companyRegistersService, times(1)).create(expectedSpec);

        assertEquals(COMPANY_NUMBER, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + COMPANY_NUMBER, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataRollBack() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);

        final String fullCompanyNumber =
                spec.getJurisdiction().getCompanyNumberPrefix() + COMPANY_NUMBER;
        when(this.randomService.getNumber(Mockito.anyInt()))
                .thenReturn(Long.valueOf(COMPANY_NUMBER));

        DataException pscStatementException = new DataException("error");
        when(companyPscStatementService.create(spec)).thenThrow(pscStatementException);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.createCompanyData(spec));

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
        verify(companyRegistersService).delete(fullCompanyNumber);
        verify(companyPscsService, times(1)).delete(fullCompanyNumber);
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
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void createCompanyDataNullSpec() {
        assertThrows(
                IllegalArgumentException.class, () -> this.testDataService.createCompanyData(null));
        verify(companyProfileService, never()).delete(any());
        verify(filingHistoryService, never()).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, never()).delete(COMPANY_NUMBER);
        verify(appointmentService, never()).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, never()).delete(COMPANY_NUMBER);
        verify(companyPscsService, never()).delete(COMPANY_NUMBER);
        verify(metricsService, never()).delete(COMPANY_NUMBER);
        verify(companyRegistersService, never()).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataProfileException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataFilingHistoryException() {
        RuntimeException ex = new RuntimeException("exception");
        when(filingHistoryService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataAuthCodeException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataAppointmentException() {
        RuntimeException ex = new RuntimeException("exception");
        when(appointmentService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataPscStatementException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataPscsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataMetricsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(metricsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(1, thrown.getSuppressed().length);
        assertEquals(ex, thrown.getSuppressed()[0]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataMultipleExceptions() {
        RuntimeException profileException = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(profileException);

        RuntimeException authCodeException = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(authCodeException);

        RuntimeException pscStatementException = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(pscStatementException);

        RuntimeException companyRegistersException = new RuntimeException("exception");
        when(companyRegistersService.delete(COMPANY_NUMBER)).thenThrow(companyRegistersException);

        DataException thrown = assertThrows(DataException.class, () ->
                this.testDataService.deleteCompanyData(COMPANY_NUMBER));

        assertEquals(4, thrown.getSuppressed().length);
        assertEquals(profileException, thrown.getSuppressed()[0]);
        assertEquals(authCodeException, thrown.getSuppressed()[1]);
        assertEquals(pscStatementException, thrown.getSuppressed()[2]);
        assertEquals(companyRegistersException, thrown.getSuppressed()[3]);

        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void createUserDataWithoutRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        UserData mockUserData = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userSpec)).thenReturn(mockUserData);

        UserData createdUserData = testDataService.createUserData(userSpec);

        assertEquals("userId", createdUserData.getId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());

        verify(roleService, never()).create(any());
    }

    @Test
    void testCreateUserWithMixedValidAndEmptyRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec validRole = new RoleSpec();
        validRole.setId("validRoleId");
        validRole.setPermissions(List.of("permission1"));

        RoleSpec emptyRole = new RoleSpec();
        emptyRole.setId("");
        emptyRole.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(validRole, emptyRole));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());

        verify(userService, never()).create(any());
    }

    @Test
    void createUserDataWithNullPassword() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword(null);

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Password is required to create a user", exception.getMessage());

        verify(userService, never()).create(any());
        verify(roleService, never()).create(any());
    }

    @Test
    void createUserDataWithEmptyPassword() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("");

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Password is required to create a user", exception.getMessage());
        verify(userService, never()).create(any());
        verify(roleService, never()).create(any());
    }

    @Test
    void createUserDataWithNullRoleId() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId(null);
        roleSpec.setPermissions(List.of("permission1"));

        userSpec.setRoles(List.of(roleSpec));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());
        verify(roleService, never()).create(any());
        verify(userService, never()).create(any());
    }

    @Test
    void createUserDataWithEmptyRoleId() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("");
        roleSpec.setPermissions(List.of("permission1"));

        userSpec.setRoles(List.of(roleSpec));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());

        verify(roleService, never()).create(any());
        verify(userService, never()).create(any());
    }

    @Test
    void createUserDataWithNullPermissions() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(null);

        userSpec.setRoles(List.of(roleSpec));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());

        verify(roleService, never()).create(any());
        verify(userService, never()).create(any());
    }

    @Test
    void createUserDataWithEmptyPermissions() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec roleSpec = new RoleSpec();
        roleSpec.setId("role-id");
        roleSpec.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(roleSpec));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());

        verify(roleService, never()).create(any());
        verify(userService, never()).create(any());
    }

    @Test
    void createUserDataWithRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec role1 = new RoleSpec();
        role1.setId("role1");
        role1.setPermissions(List.of("permission1"));

        RoleSpec role2 = new RoleSpec();
        role2.setId("role2");
        role2.setPermissions(List.of("permission2"));

        userSpec.setRoles(List.of(role1, role2));

        UserData mockUserData = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(roleService.create(role1)).thenReturn(new RoleData("role1"));
        when(roleService.create(role2)).thenReturn(new RoleData("role2"));
        when(userService.create(userSpec)).thenReturn(mockUserData);

        UserData createdUserData = testDataService.createUserData(userSpec);

        assertEquals("userId", createdUserData.getId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());

        verify(roleService, times(1)).create(role1);
        verify(roleService, times(1)).create(role2);
        verify(userService, times(1)).create(userSpec);
    }

    @Test
    void createUserDataWithMultipleInvalidRoles() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole1 = new RoleSpec();
        invalidRole1.setId("");
        invalidRole1.setPermissions(List.of("permission1"));

        RoleSpec invalidRole2 = new RoleSpec();
        invalidRole2.setId("role2");
        invalidRole2.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(invalidRole1, invalidRole2));

        DataException exception =
                assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role",
                exception.getMessage());

        verify(roleService, never()).create(any());
        verify(userService, never()).create(any());
    }

    @Test
    void deleteUserData() {
        String userId = "userId";
        User user = new User();
        user.setRoles(new ArrayList<>());

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(roleService, never()).delete(any());
    }

    @Test
    void deleteUserDataWithRoles() {
        String userId = "userId";
        List<String> roles = List.of("role1", "role2");
        User user = new User();
        user.setRoles(roles);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(roleService, times(1)).delete("role1");
        verify(roleService, times(1)).delete("role2");
        verify(userService, times(1)).delete(userId);
    }

    @Test
    void deleteUserDataWithoutExceptionWhileDeletingUser() {
        String userId = "userId";
        User user = new User();
        user.setRoles(new ArrayList<>());

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(roleService, never()).delete(any());
    }

    @Test
    void deleteUserDataWhenUserNotFound() {
        String userId = "userId";

        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        boolean result = testDataService.deleteUserData(userId);

        assertFalse(result);
        verify(userService, never()).delete(userId);
        verify(roleService, never()).delete(any());
    }

    @Test
    void createIdentityData() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("userId");
        identitySpec.setEmail("email@example.com");
        identitySpec.setVerificationSource("source");

        IdentityData mockIdentityData = new IdentityData("identityId");

        when(identityService.create(identitySpec)).thenReturn(mockIdentityData);

        IdentityData createdIdentityData = testDataService.createIdentityData(identitySpec);

        assertEquals(mockIdentityData, createdIdentityData);

        verify(identityService, times(1)).create(identitySpec);
    }

    @Test
    void createIdentityDataWithMissingUserId() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setEmail("email@example.com");
        identitySpec.setVerificationSource("source");

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createIdentityData(identitySpec));

        assertEquals("User Id is required to create an identity", exception.getMessage());
        verify(identityService, never()).create(any());
    }

    @Test
    void createIdentityDataWithMissingEmail() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("userId");
        identitySpec.setVerificationSource("source");

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createIdentityData(identitySpec));

        assertEquals("Email is required to create an identity", exception.getMessage());
        verify(identityService, never()).create(any());
    }

    @Test
    void createIdentityDataWithMissingVerificationSource() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setEmail("email@example.com");
        identitySpec.setUserId("userId");

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createIdentityData(identitySpec));

        assertEquals("Verification source is required to create an identity",
                exception.getMessage());
        verify(identityService, never()).create(any());
    }

    @Test
    void createIdentityDataThrowsException() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("userId");
        identitySpec.setEmail("email@example.com");
        identitySpec.setVerificationSource("source");

        when(identityService.create(identitySpec)).thenThrow(new RuntimeException("error"));

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createIdentityData(identitySpec));

        assertEquals("Error creating identity", exception.getMessage());
        verify(identityService, times(1)).create(identitySpec);
    }

    @Test
    void deleteIdentityData() throws DataException {
        String identityId = "identityId";

        when(identityService.delete(identityId)).thenReturn(true);

        boolean result = testDataService.deleteIdentityData(identityId);

        assertTrue(result);
        verify(identityService, times(1)).delete(identityId);
    }

    @Test
    void deleteIdentityDataWhenIdentityNotFound() throws DataException {
        String identityId = "identityId";

        when(identityService.delete(identityId)).thenReturn(false);

        boolean result = testDataService.deleteIdentityData(identityId);

        assertFalse(result);
        verify(identityService, times(1)).delete(identityId);
    }

    @Test
    void deleteIdentityDataThrowsException() {
        String identityId = "identityId";
        RuntimeException ex = new RuntimeException("error");

        when(identityService.delete(identityId)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteIdentityData(identityId));

        assertEquals("Error deleting identity", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(identityService, times(1)).delete(identityId);
    }

    @Test
    void createAcspMembersData() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");
        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        AcspMembersData acspMembersData = new AcspMembersData("memberId", "acspNumber", "userId", "active", "role");

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenReturn(acspMembersData);

        AcspMembersData result = testDataService.createAcspMembersData(spec);

        assertNotNull(result);
        assertEquals("memberId", result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("role", result.getUserRole());

        verify(acspProfileService).create(any(AcspProfileSpec.class));
        verify(acspMembersService).create(any(AcspMembersSpec.class));
    }

    @Test
    void createAcspMembersDataNullUserId() {
        AcspMembersSpec spec = new AcspMembersSpec();

        DataException exception = assertThrows(DataException.class, () -> testDataService.createAcspMembersData(spec));
        assertEquals("User ID is required to create an ACSP member", exception.getMessage());
    }

    @Test
    void createAcspMembersDataException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenThrow(new DataException("Error creating ACSP profile"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile", exception.getMessage());
    }

    @Test
    void createAcspMembersDataWithDefaultProfileValues() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        AcspMembersData acspMembersData = new AcspMembersData("memberId", "acspNumber", "userId", "active", "role");

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenReturn(acspMembersData);

        AcspMembersData result = testDataService.createAcspMembersData(spec);

        assertNotNull(result);
        assertEquals("memberId", result.getAcspMemberId());
        assertEquals("acspNumber", result.getAcspNumber());
        assertEquals("userId", result.getUserId());
        assertEquals("active", result.getStatus());
        assertEquals("role", result.getUserRole());

        verify(acspProfileService).create(any(AcspProfileSpec.class));
        verify(acspMembersService).create(any(AcspMembersSpec.class));
    }

    @Test
    void createAcspMembersDataProfileCreationException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenThrow(new DataException("Error creating ACSP profile"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile", exception.getMessage());
    }

    @Test
    void createAcspMembersDataMemberCreationException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenThrow(new DataException("Error creating ACSP member"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP member", exception.getMessage());
    }

    @Test
    void deleteAcspMembersData() throws DataException {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        when(acspMembersRepository.findById(acspMemberId)).thenReturn(Optional.of(member));

        boolean result = testDataService.deleteAcspMembersData(acspMemberId);

        assertTrue(result);
        verify(acspMembersService).delete(acspMemberId);
        verify(acspProfileService).delete("acspNumber");
    }

    @Test
    void deleteAcspMembersDataNotFound() throws DataException {
        String acspMemberId = "memberId";

        when(acspMembersRepository.findById(acspMemberId)).thenReturn(Optional.empty());

        boolean result = testDataService.deleteAcspMembersData(acspMemberId);

        assertFalse(result);
        verify(acspMembersService, never()).delete(anyString());
        verify(acspProfileService, never()).delete(anyString());
    }

    @Test
    void deleteAcspMembersDataException() {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        when(acspMembersRepository.findById(acspMemberId)).thenReturn(Optional.of(member));
        doThrow(new RuntimeException(new DataException("Error"))).when(acspMembersService).delete(acspMemberId);

        DataException exception = assertThrows(DataException.class, () -> testDataService.deleteAcspMembersData(acspMemberId));
        assertEquals("Error deleting acsp member's data", exception.getMessage());
    }

    @Test
    void createUserDataWithCompanyAuthAllowList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setIsCompanyAuthAllowList(true);

        UserData mockUserData = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userSpec)).thenReturn(mockUserData);

        UserData createdUserData = testDataService.createUserData(userSpec);

        assertEquals("userId", createdUserData.getId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());
        assertTrue(userSpec.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(1)).create(any(CompanyAuthAllowListSpec.class));
    }

    @Test
    void createUserDataWithOutCompanyAuthAllow() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        UserData mockUserData = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userSpec)).thenReturn(mockUserData);

        UserData createdUserData = testDataService.createUserData(userSpec);

        assertEquals("userId", createdUserData.getId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());
        assertNull(userSpec.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(0)).create(any(CompanyAuthAllowListSpec.class));
    }

    @Test
    void createUserDataWithNullCompanyAuthAllowList() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");
        userSpec.setIsCompanyAuthAllowList(null);

        UserData userData = new UserData("userId", "test@example.com", "Forename", "Surname");
        when(userService.create(userSpec)).thenReturn(userData);

        UserData result = testDataService.createUserData(userSpec);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("Forename", result.getForename());
        assertEquals("Surname", result.getSurname());
        assertEquals("userId", result.getId());
        assertNull(userSpec.getIsCompanyAuthAllowList());
        verify(userService, times(1)).create(userSpec);
        verify(companyAuthAllowListService, never()).create(any(CompanyAuthAllowListSpec.class));
    }

    @Test
    void deleteCompanyAuthAllowList() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn("authId");

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, times(1)).delete("authId");
    }

    @Test
    void deleteCompanyAuthAllowListWhenNull() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn(null);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithEmailAndAllowListId() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn("authId");

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, times(1)).delete("authId");
    }

    @Test
    void deleteUserDataWithEmailAndNoAllowListId() {
        String userId = "userId";
        User user = new User();
        user.setEmail("email@example.com");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);
        when(companyAuthAllowListService.getAuthId(user.getEmail())).thenReturn(null);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithNullEmail() {
        String userId = "userId";
        User user = new User();
        user.setEmail(null);

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }

    @Test
    void deleteUserDataWithEmptyEmail() {
        String userId = "userId";
        User user = new User();
        user.setEmail("");

        when(userService.getUserById(userId)).thenReturn(Optional.of(user));
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
        verify(companyAuthAllowListService, never()).delete(anyString());
    }
}
