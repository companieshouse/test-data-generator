package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DisqualificationsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.RegistersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AppointmentsResultResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyDeletionOrchestratorService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.CompanyStructurePersistenceService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyCreationOrchestratorServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String OVERSEAS_COMPANY_NUMBER = "OE123456";
    private static final String OFFICER_ID = "OFFICER_ID";
    private static final String APPOINTMENT_ID = "APPOINTMENT_ID";
    private static final String SCOTTISH_COMPANY_PREFIX = "SC";
    private static final String NI_COMPANY_PREFIX = "NI";
    private static final String AUTH_CODE = "123456";
    private static final String API_URL = "http://localhost:4001";

    @Mock private CompanyProfileService companyProfileService;
    @Mock private DataService<FilingHistory, CompanyRequest> filingHistoryService;
    @Mock private CompanyAuthCodeService companyAuthCodeService;
    @Mock private AppointmentService appointmentService;
    @Mock private DataService<CompanyMetrics, CompanyRequest> metricsService;
    @Mock private CompanyPscStatementServiceImpl companyPscStatementService;
    @Mock private CompanyPscService companyPscService;
    @Mock private RandomService randomService;
    @Mock private DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    @Mock private DataService<Disqualifications, CompanyRequest> disqualificationsService;
    @Mock private CompanyStructurePersistenceService companyStructurePersistenceService;
    @Mock private CompanySearchServiceImpl companySearchService;
    @Mock private AlphabeticalCompanySearchImpl alphabeticalCompanySearch;
    @Mock private AdvancedCompanySearchImpl advancedCompanySearch;
    @Mock private CompanyDeletionOrchestratorService companyDeletionOrchestratorService;
    @Mock private Appointment commonAppointment;

    private CompanyCreationOrchestratorServiceImpl creationService;

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

    private CompanyRequest captureCompanySpec() throws DataException {
        ArgumentCaptor<CompanyRequest> captor = ArgumentCaptor.forClass(CompanyRequest.class);
        verify(companyProfileService, times(1)).create(captor.capture());
        return captor.getValue();
    }

    private CompanyRequest captureCreatedSpec() throws DataException {
        return captureCompanySpec();
    }



    @BeforeEach
    void setUp() {
        creationService = new CompanyCreationOrchestratorServiceImpl(
                companyProfileService,
                filingHistoryService,
                companyAuthCodeService,
                appointmentService,
                metricsService,
                companyPscStatementService,
                companyPscService,
                randomService,
                companyRegistersService,
                disqualificationsService,
                companyStructurePersistenceService,
                companySearchService,
                alphabeticalCompanySearch,
                advancedCompanySearch,
                companyDeletionOrchestratorService);
        creationService.setAPIUrl(API_URL);
        creationService.setElasticSearchDeployed(false);
    }

    private void verifyCommonCompanyCreation(CompanyRequest capturedSpec,
            CompanyProfileResponse createdCompany, String expectedFullCompanyNumber,
            JurisdictionType expectedJurisdiction) throws DataException {
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(expectedJurisdiction, capturedSpec.getJurisdiction());
        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).createAppointments(capturedSpec);
        verify(companyPscStatementService, times(1)).createPscStatements(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscService, times(1)).create(capturedSpec);

        assertEquals(expectedFullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + expectedFullCompanyNumber,
                createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createInternalCompanyDefaultSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void createInternalCompanyScottishSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void createInternalCompanyNISpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.NI);
        String expectedFullCompanyNumber = NI_COMPANY_PREFIX + COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 6, expectedFullCompanyNumber);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, expectedFullCompanyNumber,
                JurisdictionType.NI);
    }

    @Test
    void createInternalCompanyWithRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        RegistersRequest directorsRegister = new RegistersRequest();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
        verify(companyRegistersService, times(1)).create(capturedSpec);
    }

    @Test
    void createInternalCompanyWithNullRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setRegisters(null);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
        verify(companyRegistersService, never()).create(any());
    }

    @Test
    void createInternalCompanyWithEmptyRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setRegisters(new ArrayList<>());
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
        verify(companyRegistersService, never()).create(any());
    }

    @Test
    void createInternalCompanyWithDisqualifications() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        DisqualificationsRequest disqSpec = new DisqualificationsRequest();
        disqSpec.setCorporateOfficer(false);
        spec.setDisqualifiedOfficers(List.of(disqSpec));
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        Disqualifications disqEntity = new Disqualifications();
        disqEntity.setId("D123");
        when(disqualificationsService.create(spec)).thenReturn(disqEntity);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        assertNotNull(result);
        verify(disqualificationsService).create(spec);
    }

    @Test
    void createInternalCompanyExistingNumber() throws Exception {
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

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        verifyCommonCompanyCreation(capturedSpec, result, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void createInternalCompanyRollBack() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.NI);
        final String fullCompanyNumber =
                spec.getJurisdiction().getCompanyNumberPrefix(spec) + COMPANY_NUMBER;

        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(spec)).thenReturn(mockAuthCode);

        RuntimeException pscStatementRuntimeException = new RuntimeException("error");
        when(companyPscStatementService.createPscStatements(spec))
                .thenThrow(pscStatementRuntimeException);

        DataException thrown = assertThrows(DataException.class,
                () -> creationService.createInternalCompany(spec));

        assertEquals(pscStatementRuntimeException, thrown.getCause());

        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointments(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verify(companyDeletionOrchestratorService).deleteCompany(fullCompanyNumber);
    }

    @Test
    void createInternalCompanyNullSpec() throws Exception {
        assertThrows(IllegalArgumentException.class,
                () -> creationService.createInternalCompany(null));
        verify(companyProfileService, never()).create(any());
        verify(companyDeletionOrchestratorService, never()).deleteCompany(any());
    }

    @Test
    void createInternalCompany_appointmentCreatedWhenNoDefaultOfficerIsNull() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(null);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createInternalCompany(spec);

        verify(appointmentService, times(1)).createAppointments(spec);
    }

    @Test
    void createInternalCompany_appointmentCreatedWhenNoDefaultOfficerIsFalse() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createInternalCompany(spec);

        verify(appointmentService, times(1)).createAppointments(spec);
    }

    @Test
    void createInternalCompany_appointmentNotCreatedWhenNoDefaultOfficerIsTrue() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(true);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createInternalCompany(spec);

        verify(appointmentService, never()).createAppointments(spec);
    }

    private CompanyProfileResponse createCompanyDataWithRegisters(CompanyRequest spec) throws Exception {
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);

        commonAppointment = new Appointment();
        commonAppointment.setOfficerId(OFFICER_ID);
        commonAppointment.setAppointmentId(APPOINTMENT_ID);

        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        return creationService.createCompany(spec);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void testCreateCompanyWithoutAlphabeticalSearch()
            throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAdvancedSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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
            throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAlphabeticalSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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
    void createInternalCompanyWithCompanyNumberPadding() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setIsPaddingCompanyNumber(true);
        spec.setJurisdiction(JurisdictionType.SCOTLAND);
        String companyNumber = "123";
        String expectedFullCompanyNumber = SCOTTISH_COMPANY_PREFIX + "000" + companyNumber;

        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(companyNumber));
        setupCompanyCreationMocks(companyNumber, 3, expectedFullCompanyNumber);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
    }

    @Test
    void createInternalCompanyWithElasticSearchDeployed() throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setAddToCompanyElasticSearchIndex(true);
        spec.setAlphabeticalSearch(true);
        spec.setAdvancedSearch(true);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        verify(companySearchService, times(1)).addCompanyIntoElasticSearchIndex(result);
        verify(alphabeticalCompanySearch, times(1)).addCompanyIntoElasticSearchIndex(result);
        verify(advancedCompanySearch, times(1)).addCompanyIntoElasticSearchIndex(result);
    }

    @Test
    void createInternalCompanyWithElasticSearchNotDeployed() throws Exception {
        creationService.setElasticSearchDeployed(false);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setAddToCompanyElasticSearchIndex(true);
        spec.setAlphabeticalSearch(true);
        spec.setAdvancedSearch(true);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        verify(companySearchService, never()).addCompanyIntoElasticSearchIndex(result);
        verify(alphabeticalCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
        verify(advancedCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
    }

    @Test
    void createInternalCompanyWithoutAlphabeticalSearch() throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setAdvancedSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        verify(companySearchService, times(1)).addCompanyIntoElasticSearchIndex(result);
        verify(alphabeticalCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
        verify(advancedCompanySearch, times(1)).addCompanyIntoElasticSearchIndex(result);
    }

    @Test
    void createInternalCompanyWithoutAdvancedSearch() throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setAlphabeticalSearch(true);
        spec.setAddToCompanyElasticSearchIndex(true);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        verify(companySearchService, times(1)).addCompanyIntoElasticSearchIndex(result);
        verify(alphabeticalCompanySearch, times(1)).addCompanyIntoElasticSearchIndex(result);
        verify(advancedCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
    }

    @Test
    void createInternalCompanyElasticSearchIndexAsFalse() throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setAddToCompanyElasticSearchIndex(false);
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse result = creationService.createInternalCompany(spec);

        verify(companySearchService, never()).addCompanyIntoElasticSearchIndex(result);
        verify(alphabeticalCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
        verify(advancedCompanySearch, never()).addCompanyIntoElasticSearchIndex(result);
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

    private void testPublicCompanySpec(PublicCompanyRequest spec) throws DataException {
        when(randomService.getNumber(8)).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any())).thenReturn(mockAuthCode);

        CompanyProfileResponse result = creationService.createPublicCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, result, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
    }

    @Test
    void buildCompanyDataStructure_success() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);

        RegistersRequest register = new RegistersRequest();
        register.setRegisterType("directors");
        register.setRegisterMovedTo("registered-office");
        spec.setRegisters(List.of(register));

        DisqualificationsRequest disqualification = new DisqualificationsRequest();
        disqualification.setDisqualificationType("court-order");
        spec.setDisqualifiedOfficers(List.of(disqualification));

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
        when(appointmentService.createAppointments(any(CompanyRequest.class)))
                .thenReturn(appointments);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(authCode);
        when(metricsService.create(any(CompanyRequest.class))).thenReturn(companyMetrics);
        when(companyPscStatementService.createPscStatements(any(CompanyRequest.class)))
                .thenReturn(pscStatements);
        when(companyPscService.create(any(CompanyRequest.class))).thenReturn(companyPscs);
        when(companyRegistersService.create(any(CompanyRequest.class))).thenReturn(companyRegisters);
        when(disqualificationsService.create(any(CompanyRequest.class)))
                .thenReturn(disqualifications);

        PopulatedCompanyDetailsResponse response =
                creationService.buildCompanyDataStructure(spec);

        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(COMPANY_NUMBER, capturedSpec.getCompanyNumber());
        assertTrue(capturedSpec.getCompanyWithPopulatedStructureOnly());

        verify(filingHistoryService, times(1)).create(capturedSpec);
        verify(appointmentService, times(1)).createAppointments(capturedSpec);
        verify(companyAuthCodeService, times(1)).create(capturedSpec);
        verify(metricsService, times(1)).create(capturedSpec);
        verify(companyPscStatementService, times(1)).createPscStatements(capturedSpec);
        verify(companyPscService, times(1)).create(capturedSpec);
        verify(companyRegistersService, times(1)).create(capturedSpec);
        verify(disqualificationsService, times(1)).create(capturedSpec);

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
    void buildCompanyDataStructure_noDefaultOfficerTrue_doesNotCreateAppointments()
            throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setNoDefaultOfficer(true);

        when(randomService.getNumber(anyInt())).thenReturn(Long.valueOf(COMPANY_NUMBER));
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(false);

        when(companyProfileService.create(any(CompanyRequest.class)))
                .thenReturn(new CompanyProfile());
        when(filingHistoryService.create(any(CompanyRequest.class)))
                .thenReturn(new FilingHistory());
        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(authCode);
        when(metricsService.create(any(CompanyRequest.class))).thenReturn(new CompanyMetrics());
        when(companyPscStatementService.createPscStatements(any(CompanyRequest.class)))
                .thenReturn(Collections.emptyList());
        when(companyPscService.create(any(CompanyRequest.class)))
                .thenReturn(Collections.emptyList());

        creationService.buildCompanyDataStructure(spec);

        CompanyRequest capturedSpec = captureCompanySpec();
        verify(appointmentService, never()).createAppointments(capturedSpec);
    }

    // ── persistCompanyStructure ────────────────────────────────────────────

    @Test
    void persistCompanyWithStructure_callsCombinedServiceAndReturnsCompanyData() throws Exception {
        CompanyWithPopulatedStructureRequest spec = new CompanyWithPopulatedStructureRequest();

        CompanyProfile profile = new CompanyProfile();
        profile.setCompanyNumber(COMPANY_NUMBER);
        spec.setCompanyProfile(profile);

        CompanyAuthCode authCode = new CompanyAuthCode();
        authCode.setAuthCode(AUTH_CODE);
        spec.setCompanyAuthCode(authCode);

        CompanyProfileResponse result = creationService.persistCompanyStructure(spec);

        assertEquals(COMPANY_NUMBER, result.getCompanyNumber());
        assertEquals(AUTH_CODE, result.getAuthCode());
        assertEquals(API_URL + "/company/" + COMPANY_NUMBER, result.getCompanyUri());
        verify(companyStructurePersistenceService)
                .persistCompanyWithStructure(spec);
    }

    @Test
    void createCompanyDataDefaultSpec() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");

        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(companyNumber, capturedSpec.getCompanyNumber());
        assertEquals(JurisdictionType.ENGLAND_WALES, capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, companyNumber,
                JurisdictionType.ENGLAND_WALES);
        verify(appointmentService, times(1)).createAppointments(spec);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(expectedFullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verifyCommonCompanyCreation(capturedSpec, createdCompany, expectedFullCompanyNumber,
                JurisdictionType.SCOTLAND);
        verify(appointmentService, times(1)).createAppointments(spec);
    }

    @Test
    void createCompanyDataWithCompanyRegisters() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        RegistersRequest directorsRegister = new RegistersRequest();
        directorsRegister.setRegisterType("directors");
        directorsRegister.setRegisterMovedTo("Companies House");
        spec.setRegisters(List.of(directorsRegister));
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, COMPANY_NUMBER);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        verifyCommonCompanyCreation(capturedSpec, createdCompany, COMPANY_NUMBER,
                JurisdictionType.ENGLAND_WALES);
        verify(companyRegistersService, times(1)).create(capturedSpec);
    }

    @Test
    void createCompanyDataRollBack() throws Exception {
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
                creationService.createCompany(spec));

        assertEquals(pscStatementRuntimeException, thrown.getCause());

        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(spec.getJurisdiction(), capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointments(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verify(companyDeletionOrchestratorService).deleteCompany(fullCompanyNumber);
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

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
        CompanyRequest capturedSpec = captureCompanySpec();
        assertEquals(fullCompanyNumber, capturedSpec.getCompanyNumber());
        assertEquals(JurisdictionType.UNITED_KINGDOM, capturedSpec.getJurisdiction());
        verify(filingHistoryService).create(capturedSpec);
        verify(companyAuthCodeService).create(capturedSpec);
        verify(appointmentService).createAppointments(capturedSpec);
        verify(companyPscStatementService).createPscStatements(capturedSpec);
        verify(metricsService).create(capturedSpec);
        verify(companyPscService).create(capturedSpec);
        assertEquals(fullCompanyNumber, createdCompany.getCompanyNumber());
        assertEquals(API_URL + "/company/" + fullCompanyNumber, createdCompany.getCompanyUri());
        assertEquals(AUTH_CODE, createdCompany.getAuthCode());
    }

    @Test
    void createCompanyDataNullSpec() throws Exception {
        assertThrows(IllegalArgumentException.class, () -> creationService.createCompany(null));
        verify(companyDeletionOrchestratorService, never()).deleteCompany(any());
        verify(companyProfileService, never()).create(any());
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
    void testCreateCompanyWithElasticSearchDeployed()
            throws Exception {
        testCreateCompanyWithElasticSearch(true, 1);
    }

    @Test
    void testCreateCompanyWithElasticSearchNotDeployed()
            throws Exception {
        testCreateCompanyWithElasticSearch(false, 0);
    }

    private void testCreateCompanyWithElasticSearch(boolean isElasticSearchDeployed,
                                                    int expectedInvocationCount)
            throws Exception {
        creationService.setElasticSearchDeployed(isElasticSearchDeployed);
        CompanyRequest spec = new CompanyRequest();
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        spec.setAddToCompanyElasticSearchIndex(true);
        spec.setAlphabeticalSearch(true);
        spec.setAdvancedSearch(true);
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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

        CompanyProfileResponse result = creationService.createCompany(spec);

        assertNotNull(result);
        verify(disqualificationsService).create(spec);
    }

    @Test
    void createCompanyData_appointmentCreatedWhenNoDefaultOfficerIsNull() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(null);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createCompany(spec);

        verify(appointmentService, times(1)).createAppointments(spec);
    }

    @Test
    void createCompanyData_appointmentCreatedWhenNoDefaultOfficerIsFalse() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(false);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createCompany(spec);

        verify(appointmentService, times(1)).createAppointments(spec);
    }

    @Test
    void createCompanyData_appointmentNotCreatedWhenNoDefaultOfficerIsTrue() throws Exception {
        CompanyRequest spec = new CompanyRequest();
        spec.setNoDefaultOfficer(true);
        CompanyAuthCode mockAuthCode = new CompanyAuthCode();
        mockAuthCode.setAuthCode(AUTH_CODE);
        when(companyAuthCodeService.create(any(CompanyRequest.class))).thenReturn(mockAuthCode);

        creationService.createCompany(spec);

        verify(appointmentService, never()).createAppointments(spec);
    }

    private void validateElasticSearch(CompanyRequest spec) throws Exception {
        spec.setJurisdiction(JurisdictionType.ENGLAND_WALES);
        spec.setCompanyStatus("administration");
        String expectedFullCompanyNumber = COMPANY_NUMBER;
        setupCompanyCreationMocks(COMPANY_NUMBER, 8, expectedFullCompanyNumber);

        CompanyProfileResponse createdCompany = creationService.createCompany(spec);
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
    void testCreateCompanyElasticSearchIndexAsFalse()
            throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        spec.setAddToCompanyElasticSearchIndex(false);
        validateElasticSearch(spec);
    }

    @Test
    void testCreateCompanyWithoutElasticSearchIndex()
            throws Exception {
        creationService.setElasticSearchDeployed(true);
        CompanyRequest spec = new CompanyRequest();
        validateElasticSearch(spec);
    }
}

