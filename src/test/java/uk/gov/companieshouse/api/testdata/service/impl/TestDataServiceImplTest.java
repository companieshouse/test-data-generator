package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.User;

import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AmlSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.*;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String OVERSEAS_COMPANY_NUMBER = "OE123456";
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
    private DataService<List<Appointment>, CompanySpec> appointmentService;
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
    private AppealsService appealsService;
    @Mock
    private DataService<CompanyRegisters, CompanySpec> companyRegistersService;
    @Mock
    private Appointment commonAppointment;
    @Mock
    private CompanySearchService companySearchService;

    @BeforeEach
    void setUp() {
        this.testDataService.setAPIUrl(API_URL);
    }

    /**
     * Sets up common mocks for creating a company.
     *
     * @param spec                      the CompanySpec to be created.
     * @param companyNumber             the raw company number (as string) to be returned by randomService.
     * @param numberDigits              the number of digits to request from randomService.
     * @param expectedFullCompanyNumber the full company number expected in the created spec.
     */
    private void setupCompanyCreationMocks(CompanySpec spec, String companyNumber, int numberDigits, String expectedFullCompanyNumber) throws DataException {
        when(randomService.getNumber(numberDigits)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);
        when(appointmentService.create(any())).thenReturn(List.of(mockAppointment));
    }

    private CompanyData createCompanyDataWithRegisters(CompanySpec spec) throws Exception {
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        commonAppointment = new Appointment();
        commonAppointment.setOfficerId(OFFICER_ID);
        commonAppointment.setAppointmentId(APPOINTMENT_ID);

        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        when(appointmentService.create(any())).thenReturn(Collections.singletonList(commonAppointment));

        return testDataService.createCompanyData(spec);
    }

    private CompanySpec captureCompanySpec() throws DataException {
        ArgumentCaptor<CompanySpec> captor = ArgumentCaptor.forClass(CompanySpec.class);
        verify(companyProfileService, times(1)).create(captor.capture());
        return captor.getValue();
    }

    private CompanySpec captureCreatedSpec() throws DataException {
        ArgumentCaptor<CompanySpec> captor = ArgumentCaptor.forClass(CompanySpec.class);
        verify(companyProfileService, times(1)).create(captor.capture());
        return captor.getValue();
    }

    private void verifyCommonCompanyCreation(CompanySpec capturedSpec, CompanyData createdCompany,
                                             String expectedFullCompanyNumber, Jurisdiction expectedJurisdiction) throws DataException {
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(expectedJurisdiction, capturedSpec.getJurisdiction());
        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).create(capturedSpec);
        verify(companyPscStatementService, times(1)).create(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscsService, times(1)).create(capturedSpec);

        assertEquals(expectedFullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + expectedFullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    /**
     * Helper to create ACSP members data.
     *
     * @param userId      the user id to set on the spec
     * @param profileData the ACSP profile data to be returned by the profile service
     * @param membersData the ACSP members data to be returned by the members service
     * @return the result of testDataService.createAcspMembersData(...)
     * @throws DataException if creation fails
     */
    private AcspMembersData createAcspMembersDataHelper(String userId,
                                                        AcspProfileData profileData,
                                                        AcspMembersData membersData, AcspProfileSpec profileSpec) throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId(userId);
        spec.setAcspProfile(profileSpec);
        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(profileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenReturn(membersData);
        return testDataService.createAcspMembersData(spec);
    }

    private void verifyAcspMembersData(AcspMembersData data,
                                       String expectedMemberId,
                                       String expectedAcspNumber,
                                       String expectedUserId,
                                       String expectedStatus,
                                       String expectedUserRole) {
        assertNotNull(data);
        assertEquals(expectedMemberId, data.getAcspMemberId());
        assertEquals(expectedAcspNumber, data.getAcspNumber());
        assertEquals(expectedUserId, data.getUserId());
        assertEquals(expectedStatus, data.getStatus());
        assertEquals(expectedUserRole, data.getUserRole());
    }

    /**
     * Helper to perform deletion of ACSP member data.
     *
     * @param acspMemberId   the member id to delete
     * @param memberOptional an Optional containing the AcspMembers if found
     * @return the result of testDataService.deleteAcspMembersData(...)
     * @throws DataException if deletion fails
     */
    private boolean deleteAcspMembersDataHelper(String acspMemberId, Optional<AcspMembers> memberOptional)
            throws DataException {
        when(acspMembersRepository.findById(acspMemberId)).thenReturn(memberOptional);
        return testDataService.deleteAcspMembersData(acspMemberId);
    }

    private void verifyDeleteCompanyData(String companyNumber) {
        verify(companyProfileService).delete(companyNumber);
        verify(filingHistoryService).delete(companyNumber);
        verify(companyAuthCodeService).delete(companyNumber);
        verify(appointmentService).delete(companyNumber);
        verify(companyPscStatementService).delete(companyNumber);
        verify(metricsService).delete(companyNumber);
        verify(companyPscsService).delete(companyNumber);
        verify(companyRegistersService).delete(companyNumber);
    }

    /**
     * Helper method to assert that deleting company data results in a DataException with the expected suppressed exceptions.
     * Also verifies that every deletion service was called exactly once.
     *
     * @param expectedExceptions varargs array of the expected suppressed RuntimeExceptions.
     */
    private void assertDeleteCompanyDataException(RuntimeException... expectedExceptions) {
        DataException thrown = assertThrows(DataException.class, () -> testDataService.deleteCompanyData(COMPANY_NUMBER));
        assertEquals(expectedExceptions.length, thrown.getSuppressed().length, "Unexpected number of suppressed exceptions");
        for (int i = 0; i < expectedExceptions.length; i++) {
            assertEquals(expectedExceptions[i], thrown.getSuppressed()[i], "Mismatch in suppressed exception at index " + i);
        }
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
    void createCompanyDataDefaultSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("administration");

        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber, Jurisdiction.ENGLAND_WALES);
    }

    @Test
    void createCompanyDataScottishSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber, Jurisdiction.SCOTLAND);
    }

    @Test
    void createCompanyDataNISpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        String expectedFullCompanyNumber = NI_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber, Jurisdiction.NI);
    }

    @Test
    void createCompanyDataSpec() throws Exception {
        final String companyNumber = "12345678";
        CompanySpec spec = new CompanySpec();
        when(randomService.getNumber(8)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(companyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);
        when(appointmentService.create(any())).thenReturn(List.of(mockAppointment));

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(companyNumber, capturedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, companyNumber, Jurisdiction.ENGLAND_WALES);
    }

    @Test
    void createCompanyDataExistingNumber() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        final String existingCompanyNumber = "555555";
        final String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        when(randomService.getNumber(anyInt()))
                .thenReturn(Long.valueOf(existingCompanyNumber))
                .thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(SCOTTISH_COMPANY_PREFIX + existingCompanyNumber)).thenReturn(true);
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
        Appointment mockAppointment = new Appointment();
        mockAppointment.setOfficerId(OFFICER_ID);
        mockAppointment.setAppointmentId(APPOINTMENT_ID);
        when(appointmentService.create(any())).thenReturn(List.of(mockAppointment));

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber, Jurisdiction.SCOTLAND);
    }

    @Test
    void createCompanyDataWithCompanyRegisters() throws Exception {
        CompanySpec spec = new CompanySpec();
        RegistersSpec directorsRegister = new RegistersSpec();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER, Jurisdiction.ENGLAND_WALES);
        verify(companyRegistersService, times(1)).create(capturedSpec);
    }

    @Test
    void createCompanyDataRollBack() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        final String fullCompanyNumber = spec.getJurisdiction().getCompanyNumberPrefix(spec) + COMPANY_NUMBER;
        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        DataException pscStatementException = new DataException("error");
        when(companyPscStatementService.create(spec)).thenThrow(pscStatementException);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createCompanyData(spec));
        assertEquals(pscStatementException, thrown.getCause());

        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).create(capturedSpec);
        verify(metricsService).create(capturedSpec);
        // Verify we roll back data
        verifyDeleteCompanyData(fullCompanyNumber);
    }

    @Test
    void createCompanyDataOverseasSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.UNITED_KINGDOM);
        when(randomService.getNumber(6)).thenReturn(Long.valueOf(OVERSEAS_COMPANY_NUMBER.substring(2)));
        final String fullCompanyNumber = OVERSEAS_COMPANY_NUMBER;
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.UNITED_KINGDOM, capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).create(capturedSpec);
        verify(companyPscStatementService).create(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verify(companyPscsService).create(capturedSpec);
        assertEquals(fullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + fullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void deleteCompanyData() throws Exception {
        testDataService.deleteCompanyData(COMPANY_NUMBER);
        verifyDeleteCompanyData(COMPANY_NUMBER);
    }

    @Test
    void createCompanyDataNullSpec() {
        assertThrows(IllegalArgumentException.class, () -> testDataService.createCompanyData(null));
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

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataFilingHistoryException() {
        RuntimeException ex = new RuntimeException("exception");
        when(filingHistoryService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataAuthCodeException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataAppointmentException() {
        RuntimeException ex = new RuntimeException("exception");
        when(appointmentService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataPscStatementException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataPscsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
    }

    @Test
    void deleteCompanyDataMetricsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(metricsService.delete(COMPANY_NUMBER)).thenThrow(ex);

        assertDeleteCompanyDataException(ex);
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

        // Expect 4 suppressed exceptions in order.
        assertDeleteCompanyDataException(profileException, authCodeException, pscStatementException, companyRegistersException);
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

        DataException exception = assertThrows(DataException.class, () -> testDataService.deleteIdentityData(identityId));

        assertEquals("Error deleting identity", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(identityService, times(1)).delete(identityId);
    }

    @Test
    void createAcspMembersData() throws DataException {
        AcspProfileSpec profileSpec = new AcspProfileSpec();
        profileSpec.setAcspNumber("acspNumber");
        profileSpec.setStatus("active");
        profileSpec.setType("limited-company");

        AcspProfileData acspProfileData =
                new AcspProfileData(profileSpec.getAcspNumber());
        AcspMembersData expectedMembersData =
                new AcspMembersData(new ObjectId(),
                        profileSpec.getAcspNumber(), "userId", "active", "role");

        AcspMembersData result = createAcspMembersDataHelper(
                "userId", acspProfileData, expectedMembersData, profileSpec);

        verifyAcspMembersData(result,
                String.valueOf(expectedMembersData.getAcspMemberId()),
                acspProfileData.getAcspNumber(), expectedMembersData.getUserId(), expectedMembersData.getStatus(), expectedMembersData.getUserRole());
        verify(acspMembersService).create(any(AcspMembersSpec.class));
        verify(acspProfileService).create(argThat(profile -> profile.getAmlDetails() == null));
    }

    @Test
    void createAcspMembersDataNullUserId() {
        AcspMembersSpec spec = new AcspMembersSpec();

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("User ID is required to create an ACSP member", exception.getMessage());
    }

    @Test
    void createAcspMembersDataException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileSpec.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataWhenProfileIsNotNull() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        AcspMembersData acspMembersData =
                new AcspMembersData(new ObjectId(), acspProfileData.getAcspNumber(), "userId", "active", "role");
        var acspStatus = "active";
        var acspType = "ltd";
        var supervisoryBody = "financial-conduct-authority-fca";
        var membershipDetails = "Membership ID: FCA654321";

        AcspProfileSpec acspProfile = new AcspProfileSpec();
        acspProfile.setStatus(acspStatus);
        acspProfile.setType(acspType);
        AmlSpec amlSpec = new AmlSpec();
        amlSpec.setSupervisoryBody(supervisoryBody);
        amlSpec.setMembershipDetails(membershipDetails);

        acspProfile.setAmlDetails(Collections.singletonList(amlSpec));

        spec.setAcspProfile(acspProfile);

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenReturn(acspMembersData);

        AcspMembersData result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersData.getAcspMemberId()),
                acspProfileData.getAcspNumber(), acspMembersData.getUserId(), acspMembersData.getStatus(), acspMembersData.getUserRole());

        verify(acspProfileService).create(acspProfile);

        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersData.getUserId().equals(membersSpec.getUserId())
                        && acspMembersData.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataWhenProfileIsNull() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        AcspMembersData acspMembersData = new AcspMembersData(new ObjectId(),
                "acspNumber", "userId", "active", "role");

        spec.setAcspProfile(null);

        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class))).thenReturn(acspMembersData);

        AcspMembersData result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersData.getAcspMemberId()),
                acspProfileData.getAcspNumber(), acspMembersData.getUserId(), acspMembersData.getStatus(), acspMembersData.getUserRole());

        verify(acspProfileService).create(argThat(profile ->
                profile.getStatus() == null
                        && profile.getType() == null
                        && profile.getAmlDetails() == null));

        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersData.getUserId().equals(membersSpec.getUserId())
                        && acspMembersData.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataProfileCreationException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileSpec.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataMemberCreationException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber");
        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class)))
                .thenThrow(new DataException("Error creating ACSP member"));

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP member",
                exception.getMessage());
    }

    @Test
    void deleteAcspMembersData() throws DataException {
        String acspMemberId = "memberId";
        AcspMembers member = new AcspMembers();
        member.setAcspNumber("acspNumber");

        boolean result = deleteAcspMembersDataHelper(acspMemberId, Optional.of(member));

        assertTrue(result);
        verify(acspMembersService).delete(acspMemberId);
        verify(acspProfileService).delete("acspNumber");
    }

    @Test
    void deleteAcspMembersDataNotFound() throws DataException {
        String acspMemberId = "memberId";

        boolean result = deleteAcspMembersDataHelper(acspMemberId, Optional.empty());

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
        doThrow(new RuntimeException(new DataException("Error")))
                .when(acspMembersService).delete(acspMemberId);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.deleteAcspMembersData(acspMemberId));
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

    @Test
    void deleteAppealsDataSuccess() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(true);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertTrue(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataFailure() throws DataException {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";

        when(appealsService.delete(companyNumber, penaltyReference)).thenReturn(false);

        boolean result = testDataService.deleteAppealsData(companyNumber, penaltyReference);

        assertFalse(result);
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void deleteAppealsDataThrowsException() {
        String companyNumber = "12345678";
        String penaltyReference = "penaltyRef";
        RuntimeException ex = new RuntimeException("error");

        when(appealsService.delete(companyNumber, penaltyReference)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteAppealsData(companyNumber, penaltyReference));

        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(appealsService, times(1)).delete(companyNumber, penaltyReference);
    }

    @Test
    void createCompanyDataWithNullRegisters() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setRegisters(null);

        CompanyData createdCompany = createCompanyDataWithRegisters(spec);
        CompanySpec capturedSpec = captureCreatedSpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER, Jurisdiction.ENGLAND_WALES);

        assertEquals(OFFICER_ID, commonAppointment.getOfficerId());
        assertEquals(APPOINTMENT_ID, commonAppointment.getAppointmentId());
    }

    @Test
    void createCompanyDataWithEmptyRegisters() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setRegisters(new ArrayList<>());

        CompanyData createdCompany = createCompanyDataWithRegisters(spec);
        CompanySpec capturedSpec = captureCreatedSpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER, Jurisdiction.ENGLAND_WALES);
    }

    @Test
    void testUpdateUserWithOneLoginCalled() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("userId");
        identitySpec.setEmail("email@example.com");
        identitySpec.setVerificationSource("source");

        IdentityData mockIdentityData = new IdentityData("identityId");
        when(identityService.create(identitySpec)).thenReturn(mockIdentityData);
        IdentityData result = testDataService.createIdentityData(identitySpec);

        assertEquals(mockIdentityData, result);
        verify(userService, times(1)).updateUserWithOneLogin("userId");
    }

    @Test
    void testUpdateUserWithOneLoginNotCalledOnException() throws DataException {
        IdentitySpec identitySpec = new IdentitySpec();
        identitySpec.setUserId("userId");
        identitySpec.setEmail("email@example.com");
        identitySpec.setVerificationSource("source");

        when(identityService.create(identitySpec)).thenThrow(new RuntimeException("error"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.createIdentityData(identitySpec));
        assertEquals("Error creating identity", exception.getMessage());
        verify(userService, never()).updateUserWithOneLogin(anyString());
    }

    @Test
    void testCreateCompanyWithElasticSearchDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testCreateCompanyWithElasticSearch(true, 1);
    }

    @Test
    void testCreateCompanyWithElasticSearchNotDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testCreateCompanyWithElasticSearch(false, 0);
    }

    private void testCreateCompanyWithElasticSearch(boolean isElasticSearchDeployed,
                                                    int expectedInvocationCount)
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(isElasticSearchDeployed);

        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, Jurisdiction.ENGLAND_WALES);

        verify(companySearchService, times(expectedInvocationCount))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void deleteCompanyDataWithElasticSearchDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);

        testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companySearchService, times(1)).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataWithElasticSearchNotDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(false);

        testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companySearchService, never()).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }
}