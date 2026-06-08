package uk.gov.companieshouse.api.testdata.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.DataService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyDeletionOrchestratorServiceImplTest {

    private static final String COMPANY_NUMBER = "12345678";
    private static final String OVERSEA_COMPANY = "FC123456";
    private static final String UK_ESTABLISHMENT_NUMBER = "BR123456";
    private static final String UK_ESTABLISHMENT_NUMBER_2 = "BR654321";

    @Mock private CompanyProfileService companyProfileService;
    @Mock private DataService<FilingHistory, CompanyRequest> filingHistoryService;
    @Mock private CompanyAuthCodeService companyAuthCodeService;
    @Mock private AppointmentService appointmentService;
    @Mock private CompanyPscStatementServiceImpl companyPscStatementService;
    @Mock private CompanyPscService companyPscService;
    @Mock private DataService<CompanyMetrics, CompanyRequest> companyMetricsService;
    @Mock private CompanyAuthAllowListService companyAuthAllowListService;
    @Mock private DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    @Mock private DataService<Disqualifications, CompanyRequest> disqualificationsService;
    @Mock private CompanySearchServiceImpl companySearchService;
    @Mock private AlphabeticalCompanySearchImpl alphabeticalCompanySearch;
    @Mock private AdvancedCompanySearchImpl advancedCompanySearch;

    private CompanyDeletionOrchestratorServiceImpl deletionService;

    @BeforeEach
    void setUp() {
        deletionService = new CompanyDeletionOrchestratorServiceImpl(
                companyProfileService,
                filingHistoryService,
                companyAuthCodeService,
                appointmentService,
                companyPscStatementService,
                companyPscService,
                companyMetricsService,
                companyAuthAllowListService,
                companyRegistersService,
                disqualificationsService,
                companySearchService,
                alphabeticalCompanySearch,
                advancedCompanySearch);
        when(companyProfileService.companyExists(anyString())).thenReturn(true);
        deletionService.setElasticSearchDeployed(false);
    }

    private void verifyDeleteCompany() {
        verify(companyProfileService).companyExists(COMPANY_NUMBER);
        verify(companyProfileService).delete(COMPANY_NUMBER);
        verify(filingHistoryService).delete(COMPANY_NUMBER);
        verify(companyAuthCodeService).delete(COMPANY_NUMBER);
        verify(appointmentService).deleteAllAppointments(COMPANY_NUMBER);
        verify(companyPscStatementService).delete(COMPANY_NUMBER);
        verify(companyMetricsService).delete(COMPANY_NUMBER);
        verify(companyPscService).delete(COMPANY_NUMBER);
        verify(companyRegistersService).delete(COMPANY_NUMBER);
    }

    /**
     * Helper that asserts deleteCompany throws a DataException containing exactly the given
     * suppressed exceptions, and verifies every deletion service was called once.
     */
    private void assertDeleteCompanyException(RuntimeException... expectedExceptions) {
        DataException thrown = assertThrows(DataException.class,
                () -> deletionService.deleteCompany(COMPANY_NUMBER));
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
        verify(companyMetricsService, times(1)).delete(COMPANY_NUMBER);
        verify(companyPscService, times(1)).delete(COMPANY_NUMBER);
        verify(companyRegistersService, times(1)).delete(COMPANY_NUMBER);
        verify(disqualificationsService, times(1)).delete(COMPANY_NUMBER);
    }

    @Test
    void deleteCompany() throws Exception {
        deletionService.deleteCompany(COMPANY_NUMBER);
        verifyDeleteCompany();
    }

    @Test
    void deleteCompanyProfileException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyFilingHistoryException() {
        RuntimeException ex = new RuntimeException("exception");
        when(filingHistoryService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyAuthCodeException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyAppointmentException() {
        RuntimeException ex = new RuntimeException("exception");
        when(appointmentService.deleteAllAppointments(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyPscStatementException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyPscsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyPscService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyMetricsException() {
        RuntimeException ex = new RuntimeException("exception");
        when(companyMetricsService.delete(COMPANY_NUMBER)).thenThrow(ex);
        assertDeleteCompanyException(ex);
    }

    @Test
    void deleteCompanyMultipleExceptions() {
        RuntimeException profileException = new RuntimeException("exception");
        when(companyProfileService.delete(COMPANY_NUMBER)).thenThrow(profileException);

        RuntimeException authCodeException = new RuntimeException("exception");
        when(companyAuthCodeService.delete(COMPANY_NUMBER)).thenThrow(authCodeException);

        RuntimeException pscStatementException = new RuntimeException("exception");
        when(companyPscStatementService.delete(COMPANY_NUMBER)).thenThrow(pscStatementException);

        RuntimeException companyRegistersException = new RuntimeException("exception");
        when(companyRegistersService.delete(COMPANY_NUMBER)).thenThrow(companyRegistersException);

        assertDeleteCompanyException(profileException, authCodeException,
                pscStatementException, companyRegistersException);
    }

    @Test
    void deleteCompanyWithUkEstablishments() throws DataException, NoDataFoundException {
        List<String> ukEstablishments = List.of("BR123456", "BR654321");
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.OVERSEA_COMPANY.getValue());
        when(companyProfileService.getCompanyProfile(OVERSEA_COMPANY))
                .thenReturn(Optional.of(companyProfile));
        when(companyProfileService.findUkEstablishmentsByParent(OVERSEA_COMPANY))
                .thenReturn(ukEstablishments);

        deletionService.deleteCompany(OVERSEA_COMPANY);

        for (String ukEstablishment : ukEstablishments) {
            verify(companyProfileService).delete(ukEstablishment);
        }
        verify(companyProfileService).delete(OVERSEA_COMPANY);
    }

    @Test
    void deleteCompanyWithoutUkEstablishments() throws DataException, NoDataFoundException {
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.LTD.getValue());
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(companyProfile));

        deletionService.deleteCompany(COMPANY_NUMBER);

        verify(companyProfileService).delete(COMPANY_NUMBER);
        verify(companyProfileService, never()).findUkEstablishmentsByParent(anyString());
    }

    @Test
    void deleteCompanyWithSuppressedExceptions() {
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER, UK_ESTABLISHMENT_NUMBER_2);
        CompanyProfile companyProfile = new CompanyProfile();
        companyProfile.setType(CompanyType.OVERSEA_COMPANY.getValue());
        when(companyProfileService.getCompanyProfile(OVERSEA_COMPANY))
                .thenReturn(Optional.of(companyProfile));
        when(companyProfileService.findUkEstablishmentsByParent(OVERSEA_COMPANY))
                .thenReturn(ukEstablishments);
        doThrow(new RuntimeException("Deletion error"))
                .when(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER);

        DataException exception = assertThrows(DataException.class,
                () -> deletionService.deleteCompany(OVERSEA_COMPANY));
        assertEquals(1, exception.getSuppressed().length);
        assertEquals("Deletion error", exception.getSuppressed()[0].getMessage());
        verify(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER);
        verify(companyProfileService).delete(UK_ESTABLISHMENT_NUMBER_2);
        verify(companyProfileService).delete(OVERSEA_COMPANY);
    }

    @Test
    void deleteCompanyWithElasticSearchDeployed() throws DataException, NoDataFoundException {
        deletionService.setElasticSearchDeployed(true);
        deletionService.deleteCompany(COMPANY_NUMBER);

        verify(companySearchService, times(1)).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(alphabeticalCompanySearch, times(1))
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(advancedCompanySearch, times(1))
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }

    @Test
    void deleteCompanyWithElasticSearchNotDeployed() throws DataException, NoDataFoundException {
        deletionService.setElasticSearchDeployed(false);
        deletionService.deleteCompany(COMPANY_NUMBER);

        verify(companySearchService, never()).deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(alphabeticalCompanySearch, never())
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
        verify(advancedCompanySearch, never())
                .deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);
    }

    @Test
    void deleteInternalCompanySuccess() throws DataException, NoDataFoundException {
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(true);

        deletionService.deleteCompany(COMPANY_NUMBER);

        verifyDeleteCompany();
    }

    @Test
    void deleteInternalCompanyDataThrowsNoDataFoundExceptionWhenCompanyNotFound() {
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(false);

        NoDataFoundException exception = assertThrows(
                NoDataFoundException.class,
                () -> deletionService.deleteCompany(COMPANY_NUMBER));

        assertEquals("Company with number " + COMPANY_NUMBER + " not found",
                exception.getMessage());
        verify(companyProfileService, times(1)).companyExists(COMPANY_NUMBER);
        verify(companyProfileService, never()).delete(anyString());
    }

    @Test
    void deleteInternalCompanyDataPropagatesException() {
        when(companyProfileService.companyExists(COMPANY_NUMBER)).thenReturn(true);
        RuntimeException cause = new RuntimeException("Mongo failure");
        doThrow(cause).when(companyProfileService).delete(COMPANY_NUMBER);

        assertThrows(DataException.class,
                () -> deletionService.deleteCompany(COMPANY_NUMBER));

        verify(companyProfileService, times(1)).companyExists(COMPANY_NUMBER);
    }
}

