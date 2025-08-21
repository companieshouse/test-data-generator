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

import jakarta.validation.ConstraintViolationException;

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
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.entity.User;

import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AmlSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertifiedCopiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;
import uk.gov.companieshouse.api.testdata.model.rest.MissingImageDeliveriesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.PostcodesData;
import uk.gov.companieshouse.api.testdata.model.rest.RegistersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TransactionService;
import uk.gov.companieshouse.api.testdata.service.UserService;

@ExtendWith(MockitoExtension.class)
class TestDataServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String OVERSEAS_COMPANY_NUMBER = "OE123456";
    private static final String OVERSEA_COMPANY = "FC123456";
    private static final String UK_ESTABLISHMENT_NUMBER = "BR123456";
    private static final String UK_ESTABLISHMENT_NUMBER_2 = "BR654321";
    private static final String AUTH_CODE = "123456";
    private static final String OFFICER_ID = "OFFICER_ID";
    private static final String APPOINTMENT_ID = "APPOINTMENT_ID";
    private static final String SCOTTISH_COMPANY_PREFIX = "SC";
    private static final String NI_COMPANY_PREFIX = "NI";
    private static final String COMPANY_CODE = "LP";
    private static final String CUSTOMER_CODE = "12345678";
    private static final String PENALTY_ID = "685abc4b9b34c84d4d2f5af6";
    private static final String PENALTY_REF = "A1234567";
    private static final String API_URL = "http://localhost:4001";
    private static final String USER_ID = "sZJQcNxzPvcwcqDwpUyRKNvVbcq";
    private static final String CERTIFICATES_ID = "CRT-123456-789012";
    private static final String AUTH_CODE_APPROVAL_ROUTE =
            "auth_code";
    private static final String CONFIRMED_STATUS = "confirmed";
    private static final String ASSOCIATION_ID = "associationId";
    private static final String CERTIFIED_COPIES_ID = "CCD-123456-789012";
    private static final String MISSING_IMAGE_DELIVERIES_ID = "MID-123456-789012";

    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private DataService<FilingHistory, CompanySpec> filingHistoryService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private DataService<CompanyMetrics, CompanySpec> metricsService;
    @Mock
    private CompanyPscStatementServiceImpl companyPscStatementService;
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
    private CompanySearchServiceImpl companySearchService;
    @Mock
    private DataService<CertificatesData, CertificatesSpec> certificatesService;
    @Mock
    private DataService<CertificatesData, CertifiedCopiesSpec> certifiedCopiesService;
    @Mock
    private DataService<CertificatesData, MissingImageDeliveriesSpec> missingImageDeliveriesService;
    @Mock
    private AccountPenaltiesService accountPenaltiesService;
    @Mock
    private AlphabeticalCompanySearchImpl alphabeticalCompanySearch;
    @Mock
    private AdvancedCompanySearchImpl advancedCompanySearch;
    @Mock
    private PostcodeService postcodeService;

    @Mock
    private TransactionService transactionService;
    @Mock
    private DataService<Disqualifications, CompanySpec> disqualificationsService;
    @Mock
    private DataService<UserCompanyAssociationData,
            UserCompanyAssociationSpec> userCompanyAssociationService;
    @Mock
    private UserCompanyAssociationRepository userCompanyAssociationRepository;

    @InjectMocks
    private TestDataServiceImpl testDataService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.testDataService.setAPIUrl(API_URL);
    }

    /**
     * Sets up common mocks for creating a company.
     *
     * @param spec                      the CompanySpec to be created.
     * @param companyNumber             the raw company number (as string) to be returned by
     *                                  randomService.
     * @param numberDigits              the number of digits to request from randomService.
     * @param expectedFullCompanyNumber the full company number expected in the created spec.
     */
    private void setupCompanyCreationMocks(CompanySpec spec, String companyNumber, int numberDigits,
                                           String expectedFullCompanyNumber) throws DataException {
        when(randomService.getNumber(numberDigits)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
    }

    private CompanyData createCompanyDataWithRegisters(CompanySpec spec) throws Exception {
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        commonAppointment = new Appointment();
        commonAppointment.setOfficerId(OFFICER_ID);
        commonAppointment.setAppointmentId(APPOINTMENT_ID);

        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

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
                                             String expectedFullCompanyNumber, Jurisdiction expectedJurisdiction)
            throws DataException {
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(expectedJurisdiction, capturedSpec.getJurisdiction());
        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).createAppointmentsWithMatchingIds(capturedSpec);
        verify(companyPscStatementService, times(1)).createPscStatements(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscsService, times(1)).create(capturedSpec);

        assertEquals(expectedFullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + expectedFullCompanyNumber,
                createdCompany.getCompanyUri());
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
    private boolean deleteAcspMembersDataHelper(String acspMemberId,
                                                Optional<AcspMembers> memberOptional)
            throws DataException {
        when(acspMembersRepository.findById(acspMemberId)).thenReturn(memberOptional);
        return testDataService.deleteAcspMembersData(acspMemberId);
    }

    private void verifyDeleteCompanyData(String companyNumber) {
        verify(companyProfileService).delete(companyNumber);
        verify(filingHistoryService).delete(companyNumber);
        verify(companyAuthCodeService).delete(companyNumber);
        verify(appointmentService).deleteAppointments(companyNumber);
        verify(appointmentService).deleteAppointmentsData(companyNumber);
        verify(companyPscStatementService).delete(companyNumber);
        verify(metricsService).delete(companyNumber);
        verify(companyPscsService).delete(companyNumber);
        verify(companyRegistersService).delete(companyNumber);
    }

    /**
     * Helper method to assert that deleting company data results in a DataException with the
     * expected suppressed exceptions. Also verifies that every deletion service was called exactly
     * once.
     *
     * @param expectedExceptions varargs array of the expected suppressed RuntimeExceptions.
     */
    private void assertDeleteCompanyDataException(RuntimeException... expectedExceptions) {
        DataException thrown = assertThrows(DataException.class,
                () -> testDataService.deleteCompanyData(COMPANY_NUMBER));
        assertEquals(expectedExceptions.length, thrown.getSuppressed().length,
                "Unexpected number of suppressed exceptions");
        for (int i = 0; i < expectedExceptions.length; i++) {
            assertEquals(expectedExceptions[i], thrown.getSuppressed()[i],
                    "Mismatch in suppressed exception at index " + i);
        }
        verify(companyProfileService, times(1)).delete(COMPANY_NUMBER);
        verify(filingHistoryService, times(1)).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService, times(1)).delete(COMPANY_NUMBER);
        verify(appointmentService, times(1)).deleteAppointments(COMPANY_NUMBER);
        verify(appointmentService, times(1)).deleteAppointmentsData(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
        verify(disqualificationsService, times(1)).delete(COMPANY_NUMBER);
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
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                Jurisdiction.ENGLAND_WALES);
    }

    @Test
    void createCompanyDataScottishSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                Jurisdiction.SCOTLAND);
    }

    @Test
    void createCompanyDataNISpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        String expectedFullCompanyNumber = NI_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                Jurisdiction.NI);
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

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(companyNumber, capturedSpec.getCompanyNumber());
        assertEquals(Jurisdiction.ENGLAND_WALES, capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, companyNumber,
                Jurisdiction.ENGLAND_WALES);
        verify(appointmentService, times(1)).createAppointmentsWithMatchingIds(spec);
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
        when(companyProfileService.companyExists(
                SCOTTISH_COMPANY_PREFIX + existingCompanyNumber)).thenReturn(true);
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);

        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                Jurisdiction.SCOTLAND);
        verify(appointmentService, times(1)).createAppointmentsWithMatchingIds(spec);
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
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                Jurisdiction.ENGLAND_WALES);
        verify(companyRegistersService, times(1)).create(capturedSpec);
    }

    @Test
    void createCompanyDataRollBack() throws DataException {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.NI);
        final String fullCompanyNumber =
                spec.getJurisdiction().getCompanyNumberPrefix(spec) + COMPANY_NUMBER;

        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(spec)).thenReturn(mockAuthCode);

        RuntimeException pscStatementRuntimeException = new RuntimeException("error");
        when(companyPscStatementService.createPscStatements(spec)).thenThrow(pscStatementRuntimeException);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createCompanyData(spec));

        assertEquals(pscStatementRuntimeException, thrown.getCause());

        CompanySpec capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointmentsWithMatchingIds(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verifyDeleteCompanyData(fullCompanyNumber);
    }

    @Test
    void createCompanyDataOverseasSpec() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.UNITED_KINGDOM);
        when(randomService.getNumber(6)).thenReturn(
                Long.valueOf(OVERSEAS_COMPANY_NUMBER.substring(2)));
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
        verify(appointmentService).createAppointmentsWithMatchingIds(capturedSpec);
        verify(companyPscStatementService).createPscStatements(capturedSpec);
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
        verify(appointmentService, never()).deleteAppointments(anyString());
        verify(appointmentService, never()).deleteAppointmentsData(anyString());
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
        when(appointmentService.deleteAppointmentsData(COMPANY_NUMBER)).thenThrow(ex);

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
        assertDeleteCompanyDataException(profileException, authCodeException, pscStatementException,
                companyRegistersException);
    }

    @Test
    void deleteCompanyDataWithUkEstablishments() throws DataException {
        List<String> ukEstablishments = List.of("BR123456", "BR654321");
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.OVERSEA_COMPANY.getValue());
        when(companyProfileService.getCompanyProfile(OVERSEA_COMPANY))
                .thenReturn(Optional.of(companyProfile));
        when(companyProfileService.findUkEstablishmentsByParent(OVERSEA_COMPANY)).thenReturn(ukEstablishments);

        testDataService.deleteCompanyData(OVERSEA_COMPANY);

        for (String ukEstablishment : ukEstablishments) {
            verify(companyProfileService).delete(ukEstablishment);
        }
        verify(companyProfileService).delete(OVERSEA_COMPANY);
    }

    @Test
    void deleteCompanyDataWithoutUkEstablishments() throws DataException {
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.LTD.getValue());
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(companyProfile));

        testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companyProfileService).delete(COMPANY_NUMBER);
        verify(companyProfileService, never()).findUkEstablishmentsByParent(anyString());
    }

    @Test
    void deleteCompanyDataWithSuppressedExceptions() {
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER, UK_ESTABLISHMENT_NUMBER_2);
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.OVERSEA_COMPANY.getValue());
        when(companyProfileService.getCompanyProfile(OVERSEA_COMPANY))
                .thenReturn(Optional.of(companyProfile));
        when(companyProfileService.findUkEstablishmentsByParent(OVERSEA_COMPANY)).thenReturn(ukEstablishments);
        doThrow(new RuntimeException("Deletion error")).when(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER);

        DataException exception = assertThrows(DataException.class, () -> testDataService.deleteCompanyData(OVERSEA_COMPANY));
        assertEquals(1, exception.getSuppressed().length);
        assertEquals("Deletion error", exception.getSuppressed()[0].getMessage());
        verify(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER);
        verify(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER_2);
        verify(companyProfileService).delete(OVERSEA_COMPANY);
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

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.deleteIdentityData(identityId));

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
                new AcspProfileData(profileSpec.getAcspNumber(),profileSpec.getName());
        AcspMembersData expectedMembersData =
                new AcspMembersData(new ObjectId(),
                        profileSpec.getAcspNumber(), "userId", "active", "role");
        AcspMembersData result = createAcspMembersDataHelper(
                "userId", acspProfileData, expectedMembersData, profileSpec);
        verifyAcspMembersData(result,
                String.valueOf(expectedMembersData.getAcspMemberId()),
                acspProfileData.getAcspNumber(), expectedMembersData.getUserId(),
                expectedMembersData.getStatus(), expectedMembersData.getUserRole());
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
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataWhenProfileIsNotNull() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber","name");
        AcspMembersData acspMembersData =
                new AcspMembersData(new ObjectId(), acspProfileData.getAcspNumber(),"userId",
                        "active", "role");
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
        acspProfileData.getAcspNumber();

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

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber","name");
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
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataMemberCreationException() throws DataException {
        AcspMembersSpec spec = new AcspMembersSpec();
        spec.setUserId("userId");

        AcspProfileData acspProfileData = new AcspProfileData("acspNumber","name");
        when(acspProfileService.create(any(AcspProfileSpec.class))).thenReturn(acspProfileData);
        when(acspMembersService.create(any(AcspMembersSpec.class)))
                .thenThrow(new DataException("Error creating ACSP member"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP member",
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
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                Jurisdiction.ENGLAND_WALES);

        assertEquals(OFFICER_ID, commonAppointment.getOfficerId());
        assertEquals(APPOINTMENT_ID, commonAppointment.getAppointmentId());
    }

    @Test
    void createCompanyDataWithEmptyRegisters() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setRegisters(new ArrayList<>());

        CompanyData createdCompany = createCompanyDataWithRegisters(spec);
        CompanySpec capturedSpec = captureCreatedSpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                Jurisdiction.ENGLAND_WALES);
    }

    @Test
    void createCertificatesData() throws DataException {
        CertificatesSpec spec = new CertificatesSpec();
        spec.setUserId(USER_ID);

        CertificatesData.CertificateEntry entry1 = new CertificatesData.CertificateEntry(
            "CRT-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesData.CertificateEntry entry2 = new CertificatesData.CertificateEntry(
            "CRT-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesData.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesData expectedCertificatesData = new CertificatesData(entries);

        when(certificatesService.create(any(CertificatesSpec.class))).thenReturn(expectedCertificatesData);
        CertificatesData result = testDataService.createCertificatesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CRT-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CRT-333333-444444", result.getCertificates().get(1).getId());

        verify(certificatesService).create(spec);
    }

    @Test
    void createCertificatesDataNullUserId() {
        CertificatesSpec spec = new CertificatesSpec();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertificatesData(spec));
        assertEquals("User ID is required to create certificates", exception.getMessage());
    }

    @Test
    void createCertificatesDataException() throws DataException {
        CertificatesSpec spec = new CertificatesSpec();
        spec.setUserId(USER_ID);

        when(certificatesService.create(any(CertificatesSpec.class)))
                .thenThrow(new DataException("Error creating certificates"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertificatesData(spec));
        assertEquals("Error creating certificates", exception.getMessage());
    }

    @Test
    void deleteCertificatesData() throws DataException {
        Certificates certificates = new Certificates();
        certificates.setId(CERTIFICATES_ID);

        when(certificatesService.delete(CERTIFICATES_ID)).thenReturn(true);
        boolean result = testDataService.deleteCertificatesData(CERTIFICATES_ID);

        assertTrue(result);
        verify(certificatesService).delete("CRT-123456-789012");
    }

    @Test
    void deleteCertificatesDataFailure() {
        when(certificatesService.delete(CERTIFICATES_ID)).thenReturn(false);
        boolean result = false;
        try {
            result = testDataService.deleteCertificatesData(CERTIFICATES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(certificatesService, times(1)).delete(CERTIFICATES_ID);
    }

    @Test
    void deleteCertificatesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(certificatesService.delete(CERTIFICATES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteCertificatesData(CERTIFICATES_ID));
        assertEquals("Error deleting certificates", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(certificatesService, times(1)).delete(CERTIFICATES_ID);
    }

    @Test
    void createCertifiedCopiesData() throws DataException {
        CertifiedCopiesSpec spec = new CertifiedCopiesSpec();
        spec.setUserId(USER_ID);

        CertificatesData.CertificateEntry entry1 = new CertificatesData.CertificateEntry(
            "CCD-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesData.CertificateEntry entry2 = new CertificatesData.CertificateEntry(
            "CCD-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesData.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesData expectedCertificatesData = new CertificatesData(entries);

        when(certifiedCopiesService.create(any(CertifiedCopiesSpec.class))).thenReturn(expectedCertificatesData);
        CertificatesData result = testDataService.createCertifiedCopiesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CCD-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CCD-333333-444444", result.getCertificates().get(1).getId());

        verify(certifiedCopiesService).create(spec);
    }

    @Test
    void createCertifiedCopiesDataNullUserId() {
        CertifiedCopiesSpec spec = new CertifiedCopiesSpec();
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createCertifiedCopiesData(spec));
        assertEquals("User ID is required to create certified copies", exception.getMessage());
    }

    @Test
    void createCertifiedCopiesDataException() throws DataException {
        CertifiedCopiesSpec spec = new CertifiedCopiesSpec();
        spec.setUserId(USER_ID);

        when(certifiedCopiesService.create(any(CertifiedCopiesSpec.class)))
            .thenThrow(new DataException("Error creating certified copies"));
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createCertifiedCopiesData(spec));
        assertEquals("Error creating certified copies", exception.getMessage());
    }

    @Test
    void deleteCertifiedCopiesData() throws DataException {
        CertifiedCopies certifiedCopies = new CertifiedCopies();
        certifiedCopies.setId(CERTIFIED_COPIES_ID);

        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenReturn(true);
        boolean result = testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID);

        assertTrue(result);
        verify(certifiedCopiesService).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void deleteCertifiedCopiesDataFailure() {
        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenReturn(false);
        boolean result = false;
        try {
            result = testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(certifiedCopiesService, times(1)).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void deleteCertifiedCopiesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(certifiedCopiesService.delete(CERTIFIED_COPIES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
            testDataService.deleteCertifiedCopiesData(CERTIFIED_COPIES_ID));
        assertEquals("Error deleting certified copies", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(certifiedCopiesService, times(1)).delete(CERTIFIED_COPIES_ID);
    }

    @Test
    void createMissingImageDeliveriesData() throws DataException {
        MissingImageDeliveriesSpec spec = new MissingImageDeliveriesSpec();
        spec.setUserId(USER_ID);

        CertificatesData.CertificateEntry entry1 = new CertificatesData.CertificateEntry(
            "MID-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesData.CertificateEntry entry2 = new CertificatesData.CertificateEntry(
            "MID-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesData.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesData expectedCertificatesData = new CertificatesData(entries);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesSpec.class))).thenReturn(expectedCertificatesData);
        CertificatesData result = testDataService.createMissingImageDeliveriesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("MID-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("MID-333333-444444", result.getCertificates().get(1).getId());

        verify(missingImageDeliveriesService).create(spec);
    }

    @Test
    void createMissingImageDeliveriesDataNullUserId() {
        MissingImageDeliveriesSpec spec = new MissingImageDeliveriesSpec();
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createMissingImageDeliveriesData(spec));
        assertEquals("User ID is required to create missing image deliveries", exception.getMessage());
    }

    @Test
    void createMissingImageDeliveriesDataException() throws DataException {
        MissingImageDeliveriesSpec spec = new MissingImageDeliveriesSpec();
        spec.setUserId(USER_ID);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesSpec.class)))
            .thenThrow(new DataException("Error creating missing image deliveries"));
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createMissingImageDeliveriesData(spec));
        assertEquals("Error creating missing image deliveries", exception.getMessage());
    }

    @Test
    void deleteMissingImageDeliveriesData() throws DataException {
        MissingImageDeliveries missingImageDeliveries = new MissingImageDeliveries();
        missingImageDeliveries.setId(MISSING_IMAGE_DELIVERIES_ID);

        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenReturn(true);
        boolean result = testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID);

        assertTrue(result);
        verify(missingImageDeliveriesService).delete(MISSING_IMAGE_DELIVERIES_ID);
    }

    @Test
    void deleteMissingImageDeliveriesDataFailure() {
        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenReturn(false);
        boolean result = false;
        try {
            result = testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID);
        } catch (DataException e) {
            throw new RuntimeException(e);
        }

        assertFalse(result);
        verify(missingImageDeliveriesService, times(1)).delete(MISSING_IMAGE_DELIVERIES_ID);
    }

    @Test
    void deleteMissingImageDeliveriesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(missingImageDeliveriesService.delete(MISSING_IMAGE_DELIVERIES_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
            testDataService.deleteMissingImageDeliveriesData(MISSING_IMAGE_DELIVERIES_ID));
        assertEquals("Error deleting missing image deliveries", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(missingImageDeliveriesService, times(1)).delete(MISSING_IMAGE_DELIVERIES_ID);
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

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createIdentityData(identitySpec));
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
        spec.setAlphabeticalSearch(true);
        spec.setAdvancedSearch(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, Jurisdiction.ENGLAND_WALES);
        verify(companySearchService, times(expectedInvocationCount))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(alphabeticalCompanySearch, times(expectedInvocationCount))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(advancedCompanySearch, times(expectedInvocationCount))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void deleteCompanyDataWithElasticSearchDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        testDataService.deleteCompanyData(COMPANY_NUMBER);

        verify(companySearchService, times(1)).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(alphabeticalCompanySearch, times(1))
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(advancedCompanySearch, times(1))
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyDataWithElasticSearchNotDeployed()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(false);
        testDataService.deleteCompanyData(COMPANY_NUMBER);
        verify(companySearchService, never()).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(alphabeticalCompanySearch, never())
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(advancedCompanySearch, never())
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }

    @Test
    void getAccountPenaltiesData() throws Exception {
        testDataService.getAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesData() throws Exception {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);
        testDataService.updateAccountPenaltiesData(PENALTY_REF, request);
        verify(accountPenaltiesService).updateAccountPenalties(PENALTY_REF, request);
    }

    @Test
    void updateAccountPenaltiesDataNotFoundException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        NoDataFoundException ex = new NoDataFoundException(
                "Error updating account penalties - not found");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void updateAccountPenaltiesDataDataException() throws NoDataFoundException, DataException {
        UpdateAccountPenaltiesRequest request = new UpdateAccountPenaltiesRequest();
        request.setCompanyCode(COMPANY_CODE);
        request.setCustomerCode(CUSTOMER_CODE);

        DataException ex = new DataException("Error updating account penalties");
        when(accountPenaltiesService.updateAccountPenalties(PENALTY_REF, request))
                .thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.updateAccountPenaltiesData(PENALTY_REF, request));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesData() throws Exception {
        testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error deleting account penalties - not found");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataException() throws NoDataFoundException {
        DataException ex = new DataException("Error deleting account penalties");
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(ConstraintViolationException.class);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals(ex.getMessage(), thrown.getMessage());
    }

    @Test
    void createPenaltyDataSuccess() throws DataException {
        PenaltySpec penaltySpec = new PenaltySpec();
        penaltySpec.setCompanyCode("LP");
        penaltySpec.setCustomerCode("NI23456");

        AccountPenaltiesData expectedData = new AccountPenaltiesData();
        expectedData.setCompanyCode("LP");
        expectedData.setCustomerCode("NI23456");

        when(accountPenaltiesService.createAccountPenalties(penaltySpec)).thenReturn(expectedData);

        AccountPenaltiesData result = testDataService.createPenaltyData(penaltySpec);

        assertEquals(expectedData, result);
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltySpec);
    }

    @Test
    void createPenaltyDataThrowsException() throws DataException {
        PenaltySpec penaltySpec = new PenaltySpec();
        penaltySpec.setCompanyCode("LP");
        penaltySpec.setCustomerCode("NI23456");

        DataException ex = new DataException("creation failed");
        when(accountPenaltiesService.createAccountPenalties(penaltySpec)).thenThrow(ex);

        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(penaltySpec));
        assertEquals("Error creating account penalties", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
        verify(accountPenaltiesService, times(1)).createAccountPenalties(penaltySpec);
    }

    @Test
    void getAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenReturn(new AccountPenaltiesData());
        AccountPenaltiesData result = testDataService.getAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).getAccountPenalties(PENALTY_ID);
    }

    @Test
    void getAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.getAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error retrieving account penalties - not found", ex.getMessage());
    }

    @Test
    void createPenaltyDataDelegatesToService() throws Exception {
        PenaltySpec spec = new PenaltySpec();
        AccountPenaltiesData data = new AccountPenaltiesData();
        when(accountPenaltiesService.createAccountPenalties(spec)).thenReturn(data);
        AccountPenaltiesData result = testDataService.createPenaltyData(spec);
        assertEquals(data, result);
        verify(accountPenaltiesService).createAccountPenalties(spec);
    }

    @Test
    void createPenaltyDataThrowsDataException() throws Exception {
        PenaltySpec spec = new PenaltySpec();
        when(accountPenaltiesService.createAccountPenalties(spec))
                .thenThrow(new DataException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.createPenaltyData(spec));
        assertEquals("Error creating account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltiesData(PENALTY_ID);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenalties(PENALTY_ID);
    }

    @Test
    void deleteAccountPenaltiesDataThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltiesDataThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenalties(PENALTY_ID))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltiesData(PENALTY_ID));
        assertEquals("Error deleting account penalties", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceDelegatesToService() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenReturn(ResponseEntity.ok().build());
        ResponseEntity<Void> result = testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
        assertNotNull(result);
        verify(accountPenaltiesService).deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF);
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsNoDataFoundException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new NoDataFoundException("not found"));
        NoDataFoundException ex = assertThrows(NoDataFoundException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty - not found", ex.getMessage());
    }

    @Test
    void deleteAccountPenaltyByReferenceThrowsDataException() throws Exception {
        when(accountPenaltiesService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF))
                .thenThrow(new RuntimeException("fail"));
        DataException ex = assertThrows(DataException.class, () ->
                testDataService.deleteAccountPenaltyByReference(PENALTY_ID, PENALTY_REF));
        assertEquals("Error deleting account penalty", ex.getMessage());
    }

    @Test
    void createCompanyWithCompanyNumberPadding() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setIsPaddingCompanyNumber(true);
        spec.setJurisdiction(Jurisdiction.SCOTLAND);
        String companyNumber = "123";
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + "000" + companyNumber;

        // Use anyInt() to allow flexibility in the argument
        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(companyNumber));

        setupCompanyCreationMocks(spec, companyNumber, 3, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                Jurisdiction.SCOTLAND);
    }

    @Test
    void testCreateCompanyWithoutAlphabeticalSearch()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAdvancedSearch(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, Jurisdiction.ENGLAND_WALES);
        verify(companySearchService, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(alphabeticalCompanySearch, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(advancedCompanySearch, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void testCreateCompanyWithoutAdvancedSearch()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAlphabeticalSearch(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyData createdCompany = testDataService.createCompanyData(spec);
        CompanySpec capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, Jurisdiction.ENGLAND_WALES);
        verify(companySearchService, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(alphabeticalCompanySearch, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(advancedCompanySearch, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void testGetPostcodesValidCountry() throws DataException {
        String country = "England";
        Postcodes mockPostcode = new Postcodes();
        mockPostcode.setBuildingNumber(12);
        mockPostcode.setThoroughfareName("First Avenue");
        mockPostcode.setThoroughfareDescriptor("High Street");
        mockPostcode.setDependentLocality("Central");
        mockPostcode.setLocalityPostTown("London");
        mockPostcode.setPretty("EC1 1BB");
        mockPostcode.setCountry(country);

        when(postcodeService.get(country)).thenReturn(List.of(mockPostcode));

        PostcodesData result = testDataService.getPostcodes(country);

        assertEquals(12, result.getBuildingNumber());
        assertEquals("First Avenue High Street", result.getFirstLine());
        assertEquals("Central", result.getDependentLocality());
        assertEquals("London", result.getPostTown());
        assertEquals("EC1 1BB", result.getPostcode());
        verify(postcodeService, times(1)).get(country);
    }

    @Test
    void testGetPostcodesInvalidCountry() throws DataException {
        String country = "InvalidCountry";

        when(postcodeService.get(country)).thenReturn(List.of());

        PostcodesData result = testDataService.getPostcodes(country);

        assertNull(result);
        verify(postcodeService, times(1)).get(country);
    }

    @Test
    void testGetPostcodesThrowsException() {
        String country = "ErrorCountry";

        when(postcodeService.get(country)).thenThrow(new RuntimeException("Error retrieving postcodes"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.getPostcodes(country));

        assertEquals("Error retrieving postcodes", exception.getMessage());
        verify(postcodeService, times(1)).get(country);
    }

    @Test
    void createCompanyDataWithDisqualifications() throws Exception {
        CompanySpec spec = new CompanySpec();
        spec.setJurisdiction(Jurisdiction.ENGLAND_WALES);
        DisqualificationsSpec disqSpec = new DisqualificationsSpec();
        disqSpec.setCorporateOfficer(false);
        spec.setDisqualifiedOfficers(List.of(disqSpec));

        setupCompanyCreationMocks(spec, COMPANY_NUMBER, 8, COMPANY_NUMBER);

        Disqualifications disqEntity = new Disqualifications();
        disqEntity.setId("D123");
        when(disqualificationsService.create(spec)).thenReturn(disqEntity);

        CompanyData result = testDataService.createCompanyData(spec);

        assertNotNull(result);
        verify(disqualificationsService).create(spec);
    }

    @Test
    void createUserCompanyAssociationData() throws DataException {
        var id = new ObjectId();
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        UserCompanyAssociationData associationData =
                new UserCompanyAssociationData(id, spec.getCompanyNumber(),
                        spec.getUserId(), null, CONFIRMED_STATUS,
                        AUTH_CODE_APPROVAL_ROUTE, null);

        when(userCompanyAssociationService.create(spec)).thenReturn(associationData);

        UserCompanyAssociationData createdAssociation =
                testDataService.createUserCompanyAssociationData(spec);

        assertNotNull(createdAssociation);
        assertEquals(id.toString(), createdAssociation.getId());
        assertEquals(USER_ID, createdAssociation.getUserId());
        assertEquals(COMPANY_NUMBER, createdAssociation.getCompanyNumber());
        assertEquals(CONFIRMED_STATUS, createdAssociation.getStatus());
        assertEquals(AUTH_CODE_APPROVAL_ROUTE,
                createdAssociation.getApprovalRoute());
        assertNull(createdAssociation.getInvitations());
        assertNull(createdAssociation.getUserEmail());

        verify(userCompanyAssociationService, times(1)).create(spec);
    }

    @Test
    void createUserCompanyAssociationDataNoUserIdOrUserEmail() {
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("A user_id or a user_email is required to create "
                + "an association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataNoCompanyNumber() {
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setUserId(USER_ID);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("Company number is required to create an "
                + "association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataException() throws DataException {
        UserCompanyAssociationSpec spec =
                new UserCompanyAssociationSpec();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        when(userCompanyAssociationService.create(spec))
                .thenThrow(new RuntimeException("Error creating the "
                        + "association"));

        DataException exception =
                assertThrows(DataException.class,
                        () -> testDataService.createUserCompanyAssociationData(spec));

        assertEquals("Error creating the association",
                exception.getMessage());
        verify(userCompanyAssociationService, times(1)).create(spec);
    }

    @Test
    void deleteUserCompanyAssociation() throws DataException {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenReturn(true);

        boolean result =
                testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID);

        assertTrue(result);
        verify(userCompanyAssociationService).delete(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationNotFound() throws DataException {
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenReturn(false);

        boolean result =
                testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID);

        assertFalse(result);
        verify(userCompanyAssociationService, times(1)).delete(ASSOCIATION_ID);
    }

    @Test
    void deleteUserCompanyAssociationException() {
        RuntimeException ex = new RuntimeException("Error deleting "
                + "association");
        when(userCompanyAssociationService.delete(ASSOCIATION_ID))
                .thenThrow(ex);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.deleteUserCompanyAssociationData(ASSOCIATION_ID));

        assertEquals("Error deleting association",
                exception.getMessage());
        verify(userCompanyAssociationService, times(1)).delete(ASSOCIATION_ID);
    }

    @Test
    void createTransactionData() throws DataException {
        TransactionsSpec transactionsSpec = new TransactionsSpec();
        transactionsSpec.setUserId("Test12454");
        transactionsSpec.setReference("ACSP Registration");
        TransactionsData txn = new TransactionsData("Test12454","ACSP Registration" ,"forename","surname","email","description","status");
        when(transactionService.create(transactionsSpec)).thenReturn(txn);
        TransactionsData result = testDataService.createTransactionData(transactionsSpec);
        assertEquals(txn, result);
    }

    @Test
    void createTransactionDataException() throws DataException {
        TransactionsSpec transactionsSpec = new TransactionsSpec();
        transactionsSpec.setUserId("Test12454");
        transactionsSpec.setReference("ACSP Registration");
        DataException ex = new DataException("creation failed");
        when(transactionService.create(transactionsSpec)).thenThrow(ex);
        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createTransactionData(transactionsSpec));
        assertEquals("Error creating transaction", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
    }
}