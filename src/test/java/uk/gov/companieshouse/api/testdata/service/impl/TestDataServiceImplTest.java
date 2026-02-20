package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.AdminPermissions;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.Certificates;
import uk.gov.companieshouse.api.testdata.model.entity.CertifiedCopies;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.MissingImageDeliveries;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AmlRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyAuthAllowListRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.request.DisqualificationsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.RegistersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;

import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.UserCompanyAssociationRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyWithPopulatedStructureService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
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
    private static final String SIC_ACTIVITY_ID = "6242bbbbafaaaa93274b2efd";
    private static final String TRANSACTION_ID = "903085-903085-903085";

    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private DataService<FilingHistory, CompanyRequest> filingHistoryService;
    @Mock
    private CompanyAuthCodeService companyAuthCodeService;
    @Mock
    private AppointmentService appointmentService;
    @Mock
    private DataService<CompanyMetrics, CompanyRequest> metricsService;
    @Mock
    private CompanyPscStatementServiceImpl companyPscStatementService;
    @Mock
    private CompanyPscService companyPscService;
    @Mock
    private RandomService randomService;
    @Mock
    private UserService userService;
    @Mock
    private AdminPermissionsRepository adminPermissionsRepository;
    @Mock
    private DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService;
    @Mock
    private AcspMembersRepository acspMembersRepository;
    @Mock
    private AcspProfileService acspProfileService;
    @Captor
    private ArgumentCaptor<CompanyRequest> specCaptor;
    @Mock
    private CompanyAuthAllowListService companyAuthAllowListService;
    @Mock
    private AppealsService appealsService;
    @Mock
    private DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    @Mock
    private Appointment commonAppointment;
    @Mock
    private CompanySearchServiceImpl companySearchService;
    @Mock
    private DataService<CombinedSicActivitiesResponse, CombinedSicActivitiesRequest> combinedSicActivitiesService;
    @Mock
    private DataService<CertificatesResponse, CertificatesRequest> certificatesService;
    @Mock
    private DataService<CertificatesResponse, CertifiedCopiesRequest> certifiedCopiesService;
    @Mock
    private DataService<CertificatesResponse, MissingImageDeliveriesRequest> missingImageDeliveriesService;
    @Mock
    private AccountPenaltiesService accountPenaltiesService;
    @Mock
    private AlphabeticalCompanySearchImpl alphabeticalCompanySearch;
    @Mock
    private AdvancedCompanySearchImpl advancedCompanySearch;
    @Mock
    private PostcodeService postcodeService;
    @Mock private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Mock
    private DataService<Disqualifications, CompanyRequest> disqualificationsService;
    @Mock
    private DataService<UserCompanyAssociationResponse,
            UserCompanyAssociationRequest> userCompanyAssociationService;
    @Mock
    private UserCompanyAssociationRepository userCompanyAssociationRepository;

    @InjectMocks
    private TestDataServiceImpl testDataService;

    @Mock
    private CompanyWithPopulatedStructureService companyWithPopulatedStructureService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.testDataService.setAPIUrl(API_URL);
    }

    /**
     * Sets up common mocks for creating a company.
     *
     * @param companyNumber             the raw company number (as string) to be returned by
     *                                  randomService.
     * @param numberDigits              the number of digits to request from randomService.
     * @param expectedFullCompanyNumber the full company number expected in the created spec.
     */
    private void setupCompanyCreationMocks(String companyNumber, int numberDigits,
                                           String expectedFullCompanyNumber) throws DataException {
        when(randomService.getNumber(numberDigits)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);
    }

    private CompanyProfileResponse createCompanyDataWithRegisters(CompanyRequest spec) throws Exception {
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        commonAppointment = new Appointment();
        commonAppointment.setOfficerId(OFFICER_ID);
        commonAppointment.setAppointmentId(APPOINTMENT_ID);

        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        return testDataService.createCompanyData(spec);
    }

    private CompanyRequest captureCompanySpec() throws DataException {
        ArgumentCaptor<CompanyRequest> captor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyProfileService, times(1)).create(captor.capture());
        return captor.getValue();
    }

    private CompanyRequest captureCreatedSpec() throws DataException {
        return captureCompanySpec();
    }

    private void verifyCommonCompanyCreation(CompanyRequest capturedSpec, CompanyProfileResponse createdCompany,
                                             String expectedFullCompanyNumber, JurisdictionType expectedJurisdiction)
            throws DataException {
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(expectedJurisdiction, capturedSpec.getJurisdiction());
        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).createAppointment(capturedSpec);
        verify(companyPscStatementService, times(1)).createPscStatements(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscService, times(1)).create(capturedSpec);

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
    private AcspMembersResponse createAcspMembersDataHelper(String userId,
                                                            AcspProfileResponse profileData,
                                                            AcspMembersResponse membersData, AcspProfileRequest profileSpec) throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId(userId);
        spec.setAcspProfile(profileSpec);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(profileData);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(membersData);
        return testDataService.createAcspMembersData(spec);
    }

    private void verifyAcspMembersData(AcspMembersResponse data,
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
        verify(appointmentService).deleteAllAppointments(companyNumber);
        verify(companyPscStatementService).delete(companyNumber);
        verify(metricsService).delete(companyNumber);
        verify(companyPscService).delete(companyNumber);
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
        verify(appointmentService, times(1)).deleteAllAppointments(COMPANY_NUMBER);
        verify(companyPscStatementService, times(1)).delete(COMPANY_NUMBER);
        verify(metricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
        verify(disqualificationsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void createCompanyDataDefaultSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");

        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void createCompanyDataScottishSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void createCompanyDataNISpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.NI);
        String expectedFullCompanyNumber = NI_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.NI);
    }

    @Test
    void createCompanyDataSpec() throws Exception {
        final String companyNumber = "12345678";
        CompanyRequest spec = new CompanyRequest();
        when(randomService.getNumber(8)).thenReturn(Long.valueOf(companyNumber));
        when(companyProfileService.companyExists(companyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(companyNumber, capturedSpec.getCompanyNumber());
        assertEquals(JurisdictionType.ENGLAND_WALES, capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, companyNumber,
                JurisdictionType.ENGLAND_WALES);
        verify(appointmentService, times(1)).createAppointment(spec);
    }

    @Test
    void createCompanyDataExistingNumber() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
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

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
        verify(appointmentService, times(1)).createAppointment(spec);
    }

    @Test
    void createCompanyDataWithCompanyRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        RegistersRequest directorsRegister = new RegistersRequest();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
        verify(companyRegistersService, times(1)).create(capturedSpec);
    }

    @Test
    void createCompanyDataRollBack() throws DataException {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.NI);
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

        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointment(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verifyDeleteCompanyData(fullCompanyNumber);
    }

    @Test
    void createCompanyDataOverseasSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.UNITED_KINGDOM);
        when(randomService.getNumber(6)).thenReturn(
                Long.valueOf(OVERSEAS_COMPANY_NUMBER.substring(2)));
        final String fullCompanyNumber = OVERSEAS_COMPANY_NUMBER;
        when(companyProfileService.companyExists(fullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(JurisdictionType.UNITED_KINGDOM, capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointment(capturedSpec);
        verify(companyPscStatementService).createPscStatements(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verify(companyPscService).create(capturedSpec);
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
        verify(appointmentService, never()).deleteAllAppointments(anyString());
        verify(companyPscStatementService, never()).delete(COMPANY_NUMBER);
        verify(companyPscService, never()).delete(COMPANY_NUMBER);
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
        when(appointmentService.deleteAllAppointments(COMPANY_NUMBER)).thenThrow(ex);

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
        when(companyPscService.delete(COMPANY_NUMBER)).thenThrow(ex);

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
    void createUserDataThrowsExceptionWhenPasswordIsNull() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword(null);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createUserData(userRequest));
        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataThrowsExceptionWhenPasswordIsEmpty() {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("");

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.createUserData(userRequest));
        assertEquals("Password is required to create a user", exception.getMessage());
    }

    @Test
    void createUserDataWithNullRoles() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithEmptyRoles() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(new ArrayList<>());

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithRolesAndPermissions() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        var entity = new AdminPermissions();
        entity.setPermissions(List.of("perm1", "perm2"));
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        assertEquals(List.of("perm1", "perm2"), userRequest.getRoles());
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserDataWithRolesAndNoPermissions() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        assertEquals(List.of("group1"), userRequest.getRoles());
        verify(userService).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createUserData_addsPermissionsWhenEntityAndPermissionsExist() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        AdminPermissions entity = new AdminPermissions();
        entity.setPermissions(List.of("perm1", "perm2"));
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("perm1", "perm2"), userRequest.getRoles());
    }

    @Test
    void createUserData_doesNotAddPermissionsWhenEntityIsNull() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(null);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("group1"), userRequest.getRoles());
    }

    @Test
    void createUserData_doesNotAddPermissionsWhenPermissionsAreNull() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1"));

        AdminPermissions entity = new AdminPermissions();
        entity.setPermissions(null);
        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("group1"), userRequest.getRoles());
    }

    @Test
    void createUserData_handlesMultipleGroupNamesWithMixedEntities() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setRoles(List.of("group1", "group2", "group3"));

        AdminPermissions entity1 = new AdminPermissions();
        entity1.setPermissions(List.of("perm1"));
        AdminPermissions entity2 = new AdminPermissions();
        entity2.setPermissions(null);

        when(adminPermissionsRepository.findByGroupName("group1")).thenReturn(entity1);
        when(adminPermissionsRepository.findByGroupName("group2")).thenReturn(null);
        when(adminPermissionsRepository.findByGroupName("group3")).thenReturn(entity2);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        testDataService.createUserData(userRequest);

        assertEquals(List.of("perm1"), userRequest.getRoles());
    }

    @Test
    void createUserDataWithCompanyAuthAllowListTrue() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(true);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(companyAuthAllowListService, times(1)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithCompanyAuthAllowListFalse() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(false);

        UserResponse userResponse = new UserResponse("id", "email", "forename", "surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals(userResponse, result);
        verify(companyAuthAllowListService, never()).create(any());
    }

    @Test
    void createAcspMembersData() throws DataException {
        AcspProfileRequest profileSpec = new AcspProfileRequest();
        profileSpec.setAcspNumber("acspNumber");
        profileSpec.setStatus("active");
        profileSpec.setType("limited-company");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber(profileSpec.getAcspNumber());
        profileEntity.setName(profileSpec.getName());
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse =
                new AcspProfileResponse(profileEntity);
        AcspMembersResponse expectedMembersData =
                new AcspMembersResponse(new ObjectId(),
                        profileSpec.getAcspNumber(), "userId", "active", "role");
        AcspMembersResponse result = createAcspMembersDataHelper(
                "userId", acspProfileResponse, expectedMembersData, profileSpec);
        verifyAcspMembersData(result,
                String.valueOf(expectedMembersData.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), expectedMembersData.getUserId(),
                expectedMembersData.getStatus(), expectedMembersData.getUserRole());
        verify(acspMembersService).create(any(AcspMembersRequest.class));
        verify(acspProfileService).create(argThat(profile -> profile.getAmlDetails() == null));
    }

    @Test
    void createAcspMembersDataNullUserId() {
        AcspMembersRequest spec = new AcspMembersRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals("User ID is required to create an ACSP member", exception.getMessage());
    }

    @Test
    void createAcspMembersDataException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataWhenProfileIsNotNull() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");
       AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        AcspMembersResponse acspMembersResponse =
                new AcspMembersResponse(new ObjectId(), acspProfileResponse.getAcspNumber(),"userId",
                        "active", "role");
        var acspStatus = "active";
        var acspType = "ltd";
        var supervisoryBody = "financial-conduct-authority-fca";
        var membershipDetails = "Membership ID: FCA654321";
        AcspProfileRequest acspProfile = new AcspProfileRequest();
        acspProfile.setStatus(acspStatus);
        acspProfile.setType(acspType);
        AmlRequest amlRequest = new AmlRequest();
        amlRequest.setSupervisoryBody(supervisoryBody);
        amlRequest.setMembershipDetails(membershipDetails);

        acspProfile.setAmlDetails(Collections.singletonList(amlRequest));

        spec.setAcspProfile(acspProfile);

        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);
        AcspMembersResponse result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());
        acspProfileResponse.getAcspNumber();

        verify(acspProfileService).create(acspProfile);

        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataWhenProfileIsNull() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        AcspMembersResponse acspMembersResponse = new AcspMembersResponse(new ObjectId(),
                "acspNumber", "userId", "active", "role");
        spec.setAcspProfile(null);

        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class))).thenReturn(acspMembersResponse);

        AcspMembersResponse result = testDataService.createAcspMembersData(spec);

        verifyAcspMembersData(result,
                String.valueOf(acspMembersResponse.getAcspMemberId()),
                acspProfileResponse.getAcspNumber(), acspMembersResponse.getUserId(), acspMembersResponse.getStatus(), acspMembersResponse.getUserRole());

        verify(acspProfileService).create(argThat(profile ->
                profile.getStatus() == null
                        && profile.getType() == null
                        && profile.getAmlDetails() == null));
        verify(acspMembersService).create(argThat(membersSpec ->
                acspMembersResponse.getUserId().equals(membersSpec.getUserId())
                        && acspMembersResponse.getAcspNumber().equals(membersSpec.getAcspNumber())
        ));
    }

    @Test
    void createAcspMembersDataProfileCreationException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        when(acspProfileService.create(any(AcspProfileRequest.class)))
                .thenThrow(new DataException("Error creating ACSP profile"));
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createAcspMembersData(spec));
        assertEquals(
                "uk.gov.companieshouse.api.testdata.exception.DataException: Error creating ACSP profile",
                exception.getMessage());
    }

    @Test
    void createAcspMembersDataMemberCreationException() throws DataException {
        AcspMembersRequest spec = new AcspMembersRequest();
        spec.setUserId("userId");

        AcspProfile profileEntity = new AcspProfile();
        profileEntity.setAcspNumber("acspNumber");
        profileEntity.setName("name");
        profileEntity.setVersion(1L);

        AcspProfileResponse acspProfileResponse = new AcspProfileResponse(profileEntity);
        when(acspProfileService.create(any(AcspProfileRequest.class))).thenReturn(acspProfileResponse);
        when(acspMembersService.create(any(AcspMembersRequest.class)))
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
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(true);

        UserResponse mockUserResponse = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userRequest)).thenReturn(mockUserResponse);

        UserResponse createdUserResponse = testDataService.createUserData(userRequest);

        assertEquals("userId", createdUserResponse.getId());
        assertEquals("email@example.com", createdUserResponse.getEmail());
        assertEquals("Forename", createdUserResponse.getForename());
        assertEquals("Surname", createdUserResponse.getSurname());
        assertTrue(userRequest.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(1)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithOutCompanyAuthAllow() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");

        UserResponse mockUserResponse = new UserResponse("userId", "email@example.com", "Forename", "Surname");

        when(userService.create(userRequest)).thenReturn(mockUserResponse);

        UserResponse createdUserResponse = testDataService.createUserData(userRequest);

        assertEquals("userId", createdUserResponse.getId());
        assertEquals("email@example.com", createdUserResponse.getEmail());
        assertEquals("Forename", createdUserResponse.getForename());
        assertEquals("Surname", createdUserResponse.getSurname());
        assertNull(userRequest.getIsCompanyAuthAllowList());

        verify(companyAuthAllowListService, times(0)).create(any(CompanyAuthAllowListRequest.class));
    }

    @Test
    void createUserDataWithNullCompanyAuthAllowList() throws DataException {
        UserRequest userRequest = new UserRequest();
        userRequest.setPassword("password");
        userRequest.setIsCompanyAuthAllowList(null);

        UserResponse userResponse = new UserResponse("userId", "test@example.com", "Forename", "Surname");
        when(userService.create(userRequest)).thenReturn(userResponse);

        UserResponse result = testDataService.createUserData(userRequest);

        assertEquals("test@example.com", result.getEmail());
        assertEquals("Forename", result.getForename());
        assertEquals("Surname", result.getSurname());
        assertEquals("userId", result.getId());
        assertNull(userRequest.getIsCompanyAuthAllowList());
        verify(userService, times(1)).create(userRequest);
        verify(companyAuthAllowListService, never()).create(any(CompanyAuthAllowListRequest.class));
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
        CompanyRequest spec = new CompanyRequest();
        spec.setRegisters(null);

        CompanyProfileResponse createdCompany = createCompanyDataWithRegisters(spec);
        CompanyRequest capturedSpec = captureCreatedSpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);

        assertEquals(OFFICER_ID, commonAppointment.getOfficerId());
        assertEquals(APPOINTMENT_ID, commonAppointment.getAppointmentId());
    }

    @Test
    void createCompanyDataWithEmptyRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setRegisters(new ArrayList<>());

        CompanyProfileResponse createdCompany = createCompanyDataWithRegisters(spec);
        CompanyRequest capturedSpec = captureCreatedSpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void createCertificatesData() throws DataException {
        CertificatesRequest spec = new CertificatesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
            "CRT-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
            "CRT-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(certificatesService.create(any(CertificatesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createCertificatesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CRT-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CRT-333333-444444", result.getCertificates().get(1).getId());

        verify(certificatesService).create(spec);
    }

    @Test
    void createCertificatesDataNullUserId() {
        CertificatesRequest spec = new CertificatesRequest();
        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createCertificatesData(spec));
        assertEquals("User ID is required to create certificates", exception.getMessage());
    }

    @Test
    void createCertificatesDataException() throws DataException {
        CertificatesRequest spec = new CertificatesRequest();
        spec.setUserId(USER_ID);

        when(certificatesService.create(any(CertificatesRequest.class)))
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
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
            "CCD-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
            "CCD-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(certifiedCopiesService.create(any(CertifiedCopiesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createCertifiedCopiesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("CCD-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("CCD-333333-444444", result.getCertificates().get(1).getId());

        verify(certifiedCopiesService).create(spec);
    }

    @Test
    void createCertifiedCopiesDataNullUserId() {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createCertifiedCopiesData(spec));
        assertEquals("User ID is required to create certified copies", exception.getMessage());
    }

    @Test
    void createCertifiedCopiesDataException() throws DataException {
        CertifiedCopiesRequest spec = new CertifiedCopiesRequest();
        spec.setUserId(USER_ID);

        when(certifiedCopiesService.create(any(CertifiedCopiesRequest.class)))
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
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        spec.setUserId(USER_ID);

        CertificatesResponse.CertificateEntry entry1 = new CertificatesResponse.CertificateEntry(
            "MID-111111-222222", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );
        CertificatesResponse.CertificateEntry entry2 = new CertificatesResponse.CertificateEntry(
            "MID-333333-444444", "2025-04-14T00:00:00Z", "2025-04-14T00:00:00Z"
        );

        List<CertificatesResponse.CertificateEntry> entries = List.of(entry1, entry2);
        CertificatesResponse expectedCertificatesResponse = new CertificatesResponse(entries);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesRequest.class))).thenReturn(expectedCertificatesResponse);
        CertificatesResponse result = testDataService.createMissingImageDeliveriesData(spec);

        assertNotNull(result);
        assertEquals(2, result.getCertificates().size());
        assertEquals("MID-111111-222222", result.getCertificates().get(0).getId());
        assertEquals("MID-333333-444444", result.getCertificates().get(1).getId());

        verify(missingImageDeliveriesService).create(spec);
    }

    @Test
    void createMissingImageDeliveriesDataNullUserId() {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createMissingImageDeliveriesData(spec));
        assertEquals("User ID is required to create missing image deliveries", exception.getMessage());
    }

    @Test
    void createMissingImageDeliveriesDataException() throws DataException {
        MissingImageDeliveriesRequest spec = new MissingImageDeliveriesRequest();
        spec.setUserId(USER_ID);

        when(missingImageDeliveriesService.create(any(MissingImageDeliveriesRequest.class)))
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
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAddToCompanyElasticSearchIndex(true);
        spec.setAlphabeticalSearch(true);
        spec.setAdvancedSearch(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, JurisdictionType.ENGLAND_WALES);
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
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCode() throws Exception {
        testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE);
        verify(accountPenaltiesService).getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE);
    }

    @Test
    void getAccountPenaltiesDataByCustomerCodeAndCompanyCodeNotFoundException() throws NoDataFoundException {
        NoDataFoundException ex = new NoDataFoundException(
                "Error retrieving account penalties - not found");
        when(accountPenaltiesService.getAccountPenalties(CUSTOMER_CODE, COMPANY_CODE))
                .thenThrow(ex);

        NoDataFoundException thrown = assertThrows(NoDataFoundException.class, () ->
                testDataService.getAccountPenaltiesData(CUSTOMER_CODE, COMPANY_CODE));
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

        AccountPenaltiesResponse expectedData = new AccountPenaltiesResponse();
        expectedData.setCompanyCode("LP");
        expectedData.setCustomerCode("NI23456");

        when(accountPenaltiesService.createAccountPenalties(penaltySpec)).thenReturn(expectedData);

        AccountPenaltiesResponse result = testDataService.createPenaltyData(penaltySpec);

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
                .thenReturn(new AccountPenaltiesResponse());
        AccountPenaltiesResponse result = testDataService.getAccountPenaltiesData(PENALTY_ID);
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
        AccountPenaltiesResponse data = new AccountPenaltiesResponse();
        when(accountPenaltiesService.createAccountPenalties(spec)).thenReturn(data);
        AccountPenaltiesResponse result = testDataService.createPenaltyData(spec);
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
        CompanyRequest spec = new CompanyRequest();
        spec.setIsPaddingCompanyNumber(true);
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
        String companyNumber = "123";
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + "000" + companyNumber;

        // Use anyInt() to allow flexibility in the argument
        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(companyNumber));

        setupCompanyCreationMocks(companyNumber, 3, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void testCreateCompanyWithoutAlphabeticalSearch()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAdvancedSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, JurisdictionType.ENGLAND_WALES);
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
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAlphabeticalSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, JurisdictionType.ENGLAND_WALES);
        verify(companySearchService, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(alphabeticalCompanySearch, times(1))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(advancedCompanySearch, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void testGetPostcodesValidCountry() throws DataException {
        var country = "England";
        var streetName = "First Avenue";
        var streetDescriptor = "High Street";
        var dependentLocality = "London Road";
        var postTown = "London";
        var postcodePretty = "EC1 1BB";
        var buildingNumber = 12;
        Postcodes mockPostcode = new Postcodes();
        mockPostcode.setBuildingNumber(buildingNumber);
        Postcodes.Thoroughfare thoroughfare = new Postcodes.Thoroughfare();
        thoroughfare.setName(streetName);
        thoroughfare.setDescriptor(streetDescriptor);
        mockPostcode.setThoroughfare(thoroughfare);
        Postcodes.Locality locality = new Postcodes.Locality();
        locality.setDependentLocality(dependentLocality);
        locality.setPostTown(postTown);
        mockPostcode.setLocality(locality);
        Postcodes.PostcodeDetails postcodeDetails = new Postcodes.PostcodeDetails();
        postcodeDetails.setPretty(postcodePretty);
        mockPostcode.setPostcode(postcodeDetails);
        mockPostcode.setCountry(country);

        when(postcodeService.getPostcodeByCountry(country)).thenReturn(List.of(mockPostcode));
        PostcodesResponse result = testDataService.getPostcodes(country);
        assertEquals(buildingNumber, result.getBuildingNumber());
        assertEquals(streetName + " " + streetDescriptor, result.getFirstLine());
        assertEquals(dependentLocality, result.getDependentLocality());
        assertEquals(postTown, result.getPostTown());
        assertEquals(postcodePretty, result.getPostcode());
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void testGetPostcodesInvalidCountry() throws DataException {
        String country = "InvalidCountry";

        when(postcodeService.getPostcodeByCountry(country)).thenReturn(List.of());

        PostcodesResponse result = testDataService.getPostcodes(country);

        assertNull(result);
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void testGetPostcodesThrowsException() {
        String country = "ErrorCountry";

        when(postcodeService.getPostcodeByCountry(country)).thenThrow(new RuntimeException("Error retrieving postcodes"));

        DataException exception = assertThrows(DataException.class, () -> testDataService.getPostcodes(country));

        assertEquals("Error retrieving postcodes", exception.getMessage());
        verify(postcodeService, times(1)).getPostcodeByCountry(country);
    }

    @Test
    void createCompanyDataWithDisqualifications() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        DisqualificationsRequest disqSpec = new DisqualificationsRequest();
        disqSpec.setCorporateOfficer(false);
        spec.setDisqualifiedOfficers(List.of(disqSpec));

        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        Disqualifications disqEntity = new Disqualifications();
        disqEntity.setId("D123");
        when(disqualificationsService.create(spec)).thenReturn(disqEntity);

        CompanyProfileResponse result = testDataService.createCompanyData(spec);

        assertNotNull(result);
        verify(disqualificationsService).create(spec);
    }

    @Test
    void createUserCompanyAssociationData() throws DataException {
        var id = new ObjectId();
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);
        spec.setCompanyNumber(COMPANY_NUMBER);

        UserCompanyAssociationResponse associationData =
                new UserCompanyAssociationResponse(id, spec.getCompanyNumber(),
                        spec.getUserId(), null, CONFIRMED_STATUS,
                        AUTH_CODE_APPROVAL_ROUTE, null);

        when(userCompanyAssociationService.create(spec)).thenReturn(associationData);

        UserCompanyAssociationResponse createdAssociation =
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
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("A user_id or a user_email is required to create "
                + "an association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataNoCompanyNumber() {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
        spec.setUserId(USER_ID);

        DataException exception = assertThrows(DataException.class,
                () -> testDataService.createUserCompanyAssociationData(spec));
        assertEquals("Company number is required to create an "
                + "association", exception.getMessage());
    }

    @Test
    void createUserCompanyAssociationDataException() throws DataException {
        UserCompanyAssociationRequest spec =
                new UserCompanyAssociationRequest();
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
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        TransactionsResponse txn = new TransactionsResponse("Test12454","email@email.com" ,"forename","surname","resumeURI","status", "250788-250788-250788");
        when(transactionService.create(transactionsRequest)).thenReturn(txn);
        TransactionsResponse result = testDataService.createTransactionData(transactionsRequest);
        assertEquals(txn, result);
    }

    @Test
    void createTransactionDataException() throws DataException {
        TransactionsRequest transactionsRequest = new TransactionsRequest();
        transactionsRequest.setUserId("Test12454");
        transactionsRequest.setReference("ACSP Registration");
        DataException ex = new DataException("creation failed");
        when(transactionService.create(transactionsRequest)).thenThrow(ex);
        DataException thrown = assertThrows(DataException.class, () ->
                testDataService.createTransactionData(transactionsRequest));
        assertEquals("Error creating transaction", thrown.getMessage());
        assertEquals(ex, thrown.getCause());
    }

    @Test
    void deleteTransactionData() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(true);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertTrue(result);
        verify(transactionService).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionDataFailure() throws DataException {
        when(transactionService.delete(TRANSACTION_ID)).thenReturn(false);

        boolean result = testDataService.deleteTransaction(TRANSACTION_ID);

        assertFalse(result);
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void deleteTransactionThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(transactionService.delete(TRANSACTION_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
                testDataService.deleteTransaction(TRANSACTION_ID));

        assertEquals("Error deleting transaction", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(transactionService, times(1)).delete(TRANSACTION_ID);
    }

    @Test
    void createCompanyData_appointmentCreatedWhenNoDefaultOfficerIsNull() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(null);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        testDataService.createCompanyData(spec);

        verify(appointmentService, times(1)).createAppointment(spec);
    }

    @Test
    void createCompanyData_appointmentCreatedWhenNoDefaultOfficerIsFalse() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        testDataService.createCompanyData(spec);

        verify(appointmentService, times(1)).createAppointment(spec);
    }

    @Test
    void createCompanyData_appointmentNotCreatedWhenNoDefaultOfficerIsTrue() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(true);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        testDataService.createCompanyData(spec);

        verify(appointmentService, never()).createAppointment(spec);
    }

    @Test
    void createCombinedSicActivitiesData() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setActivityDescription("Braunkohle waschen");
        spec.setSicDescription("Abbau von Braunkohle");
        spec.setIsChActivity(false);
        spec.setActivityDescriptionSearchField("braunkohle waschen");

        CombinedSicActivitiesResponse expectedData =
            new CombinedSicActivitiesResponse(
                new ObjectId().toHexString(),
                "12345",
                "Abbau von Braunkohle"
            );

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
            .thenReturn(expectedData);

        CombinedSicActivitiesResponse result =
            testDataService.createCombinedSicActivitiesData(spec);

        assertNotNull(result);
        assertEquals("12345", result.getSicCode());
        assertEquals("Abbau von Braunkohle", result.getSicDescription());

        verify(combinedSicActivitiesService).create(spec);
    }

    @Test
    void createCombinedSicActivitiesDataException() throws DataException {
        CombinedSicActivitiesRequest spec = new CombinedSicActivitiesRequest();
        spec.setSicDescription("Test Sic Description");

        when(combinedSicActivitiesService.create(any(CombinedSicActivitiesRequest.class)))
            .thenThrow(new DataException("Error creating Sic code and keyword"));

        DataException exception = assertThrows(DataException.class,
            () -> testDataService.createCombinedSicActivitiesData(spec));

        assertEquals("Error creating Sic code and keyword", exception.getMessage());
    }

    @Test
    void deleteCombinedSicActivitiesData() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(true);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertTrue(result);
        verify(combinedSicActivitiesService).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesDataFailure() throws DataException {
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenReturn(false);

        boolean result = testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID);

        assertFalse(result);
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void deleteCombinedSicActivitiesThrowsException() {
        RuntimeException ex = new RuntimeException("error");
        when(combinedSicActivitiesService.delete(SIC_ACTIVITY_ID)).thenThrow(ex);

        DataException exception = assertThrows(DataException.class, () ->
            testDataService.deleteCombinedSicActivitiesData(SIC_ACTIVITY_ID));

        assertEquals("Error deleting appeals data", exception.getMessage());
        assertEquals(ex, exception.getCause());
        verify(combinedSicActivitiesService, times(1)).delete(SIC_ACTIVITY_ID);
    }

    @Test
    void createPublicCompany() throws DataException {
        PublicCompanyRequest spec = new PublicCompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        testPublicCompanySpec(spec);
    }

    @Test
    void createPublicCompanyWithNullSpec() throws DataException {
        PublicCompanyRequest spec = new PublicCompanyRequest();
        testPublicCompanySpec(spec);
    }

    private void testPublicCompanySpec(PublicCompanyRequest spec) throws DataException{
        String expectedFullCompanyNumber = COMPANY_NUMBER;

        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(expectedFullCompanyNumber)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyProfileResponse createdCompany = testDataService.createPublicCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void findOrCreateCompanyAuthCode_successReturnsAuthCode() throws Exception {
        CompanyAuthCode expected = new CompanyAuthCode();
        expected.setId(COMPANY_NUMBER);
        expected.setAuthCode("999999");

        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenReturn(expected);

        CompanyAuthCode actual = testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER);

        assertSame(expected, actual);
    }

    @Test
    void findOrCreateCompanyAuthCode_profileNotFoundIsMappedToNoDataFoundException() throws Exception {
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER))
                .thenThrow(new NoDataFoundException("profile missing"));

        NoDataFoundException ex = assertThrows(NoDataFoundException.class,
                () -> testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER));

        assertEquals("Company profile not found when finding or creating auth code", ex.getMessage());
    }

    @Test
    void findOrCreateCompanyAuthCode_otherExceptionIsWrappedInDataException() throws Exception {
        RuntimeException cause = new RuntimeException("boom");
        when(companyAuthCodeService.findOrCreate(COMPANY_NUMBER)).thenThrow(cause);

        DataException ex = assertThrows(DataException.class,
                () -> testDataService.findOrCreateCompanyAuthCode(COMPANY_NUMBER));

        assertEquals("Error finding or creating company auth code", ex.getMessage());
        // ensure original cause is preserved
        assertSame(cause, ex.getCause());
    }

    @Test
    void testCreateCompanyElasticSearchIndexAsFalse()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setAddToCompanyElasticSearchIndex(false);
        validateElasticSearch(spec);
    }

    @Test
    void testCreateCompanyWithoutElasticSearchIndex()
            throws DataException, ApiErrorResponseException, URIValidationException {
        testDataService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        validateElasticSearch(spec);
    }

    private void validateElasticSearch(CompanyRequest spec) throws DataException, ApiErrorResponseException, URIValidationException {
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany,
                expectedFullCompanyNumber, JurisdictionType.ENGLAND_WALES);
        verify(companySearchService, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(alphabeticalCompanySearch, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
        verify(advancedCompanySearch, times(0))
                .addCompanyIntoElasticSearchIndex(createdCompany);
    }

    @Test
    void getCompanyProfile_success_createsAndReturnsCompanyDetails() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);

        // Exercise optional paths too
        RegistersRequest register = new RegistersRequest();
        register.setRegisterType("directors");
        register.setRegisterMovedTo("registered-office");
        spec.setRegisters(List.of(register));

        DisqualificationsRequest disqualification = new DisqualificationsRequest();
        disqualification.setDisqualificationType("court-order");
        spec.setDisqualifiedOfficers(List.of(disqualification));

        // company number generation loop
        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(false);

        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setCompanyNumber(COMPANY_NUMBER);

        FilingHistory filingHistory = new FilingHistory();

        Appointment appointment = new Appointment();
        appointment.setOfficerId(OFFICER_ID);
        appointment.setAppointmentId(APPOINTMENT_ID);
        AppointmentsResultResponse appointments = new AppointmentsResultResponse();
        appointments.setAppointment(List.of(appointment));

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(AUTH_CODE);

        CompanyMetrics companyMetrics = new CompanyMetrics();

        List<CompanyPscStatement> pscStatements = List.of(new CompanyPscStatement());

        List<CompanyPscs> companyPscs = List.of(new CompanyPscs());

        CompanyRegisters companyRegisters = new CompanyRegisters();
        Disqualifications disqualifications = new Disqualifications();

        when(companyProfileService.create(any(CompanyRequest.class))).thenReturn(companyProfile);
        when(filingHistoryService.create(any(CompanyRequest.class))).thenReturn(filingHistory);
        when(appointmentService.createAppointment(any(CompanyRequest.class))).thenReturn(appointments);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(authCode);
        when(metricsService.create(any(CompanyRequest.class))).thenReturn(companyMetrics);
        when(companyPscStatementService.createPscStatements(any(CompanyRequest.class))).thenReturn(pscStatements);
        when(companyPscService.create(any(CompanyRequest.class))).thenReturn(companyPscs);
        when(companyRegistersService.create(any(CompanyRequest.class))).thenReturn(companyRegisters);
        when(disqualificationsService.create(any(CompanyRequest.class))).thenReturn(disqualifications);

        PopulatedCompanyDetailsResponse response = testDataService.getCompanyDataStructureBeforeSavingInMongoDb(spec);

        // Capture the spec that was used for creation
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(COMPANY_NUMBER, capturedSpec.getCompanyNumber());
        assertEquals(Boolean.TRUE, capturedSpec.getCompanyWithPopulatedStructureOnly());

        // Verify calls
        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).createAppointment(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscStatementService, times(1)).createPscStatements(capturedSpec);
        verify(companyPscService, times(1)).create(capturedSpec);
        verify(companyRegistersService, times(1)).create(capturedSpec);
        verify(disqualificationsService, times(1)).create(capturedSpec);

        // Verify response populated
        assertSame(companyProfile, response.getCompanyProfile());
        assertSame(filingHistory, response.getFilingHistory());
        assertSame(appointments, response.getAppointmentsData());
        assertSame(authCode, response.getCompanyAuthCode());
        assertSame(companyMetrics, response.getCompanyMetrics());
        assertSame(pscStatements, response.getCompanyPscStatement());
        assertSame(companyPscs, response.getCompanyPscs());
        assertSame(companyRegisters, response.getCompanyRegisters());
        assertSame(disqualifications, response.getDisqualifications());
    }

    @Test
    void getCompanyDataStructureBeforeSavingInMongoDb_noDefaultOfficerTrue_doesNotCreateAppointments() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setNoDefaultOfficer(true);

        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(false);

        when(companyProfileService.create(any(CompanyRequest.class))).thenReturn(new CompanyProfile());
        when(filingHistoryService.create(any(CompanyRequest.class))).thenReturn(new FilingHistory());

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(authCode);

        when(metricsService.create(any(CompanyRequest.class))).thenReturn(new CompanyMetrics());
        when(companyPscStatementService.createPscStatements(any(CompanyRequest.class)))
                .thenReturn(Collections.emptyList());
        when(companyPscService.create(any(CompanyRequest.class))).thenReturn(Collections.emptyList());

        testDataService.getCompanyDataStructureBeforeSavingInMongoDb(spec);

        CompanyRequest capturedSpec = captureCompanySpec();
        verify(appointmentService, never()).createAppointment(capturedSpec);
    }

    @Test
    void createCompanyWithStructure_callsCombinedServiceAndReturnsCompanyData() throws Exception {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();

        CompanyProfile profile = new CompanyProfile();
        profile.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyProfile(profile);

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(AUTH_CODE);
        spec.setCompanyAuthCode(authCode);

        CompanyProfileResponse result = testDataService.createCompanyWithStructure(spec);

        assertEquals(COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals(AUTH_CODE, result.getAuthCode());
        assertEquals(API_URL + "/company/" + COMPANY_NUMBER, result.getCompanyUri());
    }

    @Test
    void getAcspProfileDataReturnsProfile() throws NoDataFoundException {
        String acspNumber = "AP000036";

        // Prepare the mock AcspProfile
        AcspProfile profile = new AcspProfile();
        profile.setId(acspNumber);
        profile.setAcspNumber(acspNumber);
        profile.setName("Test ACSP Company");
        profile.setStatus("active");

        // Mock the acspProfileService
        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.of(profile));

        // Call the service method
        Optional<AcspProfile> result = testDataService.getAcspProfileData(acspNumber);

        // Verify results
        assertNotNull(result);
        assertTrue(result.isPresent());

        AcspProfile returnedProfile = result.get();
        assertEquals(acspNumber, returnedProfile.getId());
        assertEquals(acspNumber, returnedProfile.getAcspNumber());
        assertEquals("Test ACSP Company", returnedProfile.getName());
        assertEquals("active", returnedProfile.getStatus());

        // Verify interaction with the mock
        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }

    @Test
    void getAcspProfileDataReturnsEmpty() throws NoDataFoundException {
        String acspNumber = "NON_EXISTENT";

        // Mock empty response
        when(acspProfileService.getAcspProfile(acspNumber)).thenReturn(Optional.empty());

        // Call the service method
        Optional<AcspProfile> result = testDataService.getAcspProfileData(acspNumber);

        // Verify results
        assertNotNull(result);
        assertTrue(result.isEmpty());

        // Verify interaction with the mock
        verify(acspProfileService, times(1)).getAcspProfile(acspNumber);
    }


}
