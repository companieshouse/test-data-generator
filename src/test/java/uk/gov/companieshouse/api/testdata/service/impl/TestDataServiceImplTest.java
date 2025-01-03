package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.*;
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
import uk.gov.companieshouse.api.testdata.model.entity.*;
import uk.gov.companieshouse.api.testdata.model.rest.*;
import uk.gov.companieshouse.api.testdata.service.*;

import java.util.ArrayList;
import java.util.List;

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
    private DataService<FilingHistory,CompanySpec> filingHistoryService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private DataService<Appointment,CompanySpec> appointmentService;
    @Mock
    private DataService<CompanyMetrics,CompanySpec> metricsService;
    @Mock
    private DataService<CompanyPscStatement,CompanySpec> companyPscStatementService;
    @Mock
    private DataService<CompanyPscs,CompanySpec> companyPscsService;

    @Mock
    private RandomService randomService;
    @Mock
    private UserService userService;
    @Mock
    private RoleService roleService;
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
        assertThrows(IllegalArgumentException.class, () -> this.testDataService.createCompanyData(null));
        verify(companyProfileService, never()).delete(any());
        verify(filingHistoryService, never()).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, never()).delete(COMPANY_NUMBER);
        verify(appointmentService, never()).delete(COMPANY_NUMBER);
        verify(companyPscStatementService, never()).delete(COMPANY_NUMBER);
        verify(companyPscsService, never()).delete(COMPANY_NUMBER);
        verify(metricsService, never()).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataProfileException() throws DataException {
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
    void deleteCompanyDataFilingHistoryException() throws DataException {
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
    void deleteCompanyDataAuthCodeException() throws DataException {
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
    void deleteCompanyDataAppointmentException() throws DataException {
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
    void deleteCompanyDataPscStatementException() throws DataException {
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
    void deleteCompanyDataPscsException() throws DataException {
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
    void deleteCompanyDataMetricsException() throws DataException {
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
    void deleteCompanyDataMultipleExceptions() throws DataException {
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

    @Test
    void createUserData() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        UserData mockUserData = new UserData("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userSpec)).thenReturn(mockUserData);

        UserData createdUserData = testDataService.createUserData(userSpec);

        assertEquals("userId", createdUserData.getUserId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());
    }

    @Test
    void createUserDataThrowsException() throws DataException {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        when(userService.create(userSpec)).thenThrow(new RuntimeException("Database error"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Database error", exception.getMessage());
    }

    @Test
    void deleteUserData() throws DataException {
        String userId = "userId";
        Users user = new Users();
        user.setRoles(new ArrayList<>());

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(userService, times(1)).delete(userId);
    }


    @Test
    void deleteUserDataThrowsException() throws DataException {
        String userId = "userId";

        when(userService.getUserById(userId)).thenReturn(null);

        DataException exception = assertThrows(DataException.class, () -> testDataService.deleteUserData(userId));

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void createUserDataWithInvalidRoles() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole = new RoleSpec();
        invalidRole.setId("");
        invalidRole.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(invalidRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }


    @Test
    void deleteUserDataWithRoles() throws DataException {
        String userId = "userId";
        List<String> roles = List.of("role1", "role2");
        Users user = new Users();
        user.setRoles(roles);

        when(userService.getUserById(userId)).thenReturn(user);
        when(userService.delete(userId)).thenReturn(true);

        boolean result = testDataService.deleteUserData(userId);

        assertTrue(result);
        verify(roleService, times(1)).delete("role1");
        verify(roleService, times(1)).delete("role2");
        verify(userService, times(1)).delete(userId);
    }

    @Test
    void testCreateUserWithMixedValidAndEmptyRoles() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec validRole = new RoleSpec();
        validRole.setId("validRoleId");
        validRole.setPermissions(List.of("permission1"));

        RoleSpec emptyRole = new RoleSpec();
        emptyRole.setId("");
        emptyRole.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(validRole, emptyRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void createUserDataWithNullPassword() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword(null);

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataWithEmptyPassword() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("");

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataWithNullRoleId() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole = new RoleSpec();
        invalidRole.setId(null);
        invalidRole.setPermissions(List.of("permission1"));

        userSpec.setRoles(List.of(invalidRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void createUserDataWithEmptyRoleId() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole = new RoleSpec();
        invalidRole.setId("");
        invalidRole.setPermissions(List.of("permission1"));

        userSpec.setRoles(List.of(invalidRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void createUserDataWithNullPermissions() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole = new RoleSpec();
        invalidRole.setId("role-id");
        invalidRole.setPermissions(null);

        userSpec.setRoles(List.of(invalidRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
    }

    @Test
    void createUserDataWithEmptyPermissions() {
        UserSpec userSpec = new UserSpec();
        userSpec.setPassword("password");

        RoleSpec invalidRole = new RoleSpec();
        invalidRole.setId("role-id");
        invalidRole.setPermissions(new ArrayList<>());

        userSpec.setRoles(List.of(invalidRole));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createUserData(userSpec));

        assertEquals("Role ID and permissions are required to create a role", exception.getMessage());
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

        assertEquals("userId", createdUserData.getUserId());
        assertEquals("email@example.com", createdUserData.getEmail());
        assertEquals("Forename", createdUserData.getForename());
        assertEquals("Surname", createdUserData.getSurname());

        verify(roleService, times(1)).create(role1);
        verify(roleService, times(1)).create(role2);
        verify(userService, times(1)).create(userSpec);
    }
}