package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyCreationOrchestratorService;
import uk.gov.companieshouse.api.testdata.service.CompanyDeletionOrchestratorService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.api.testdata.service.CompanyWithPopulatedStructureService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CompanyCreationOrchestratorServiceImpl implements CompanyCreationOrchestratorService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final int COMPANY_NUMBER_LENGTH = 8;

    private final CompanyProfileService companyProfileService;
    private final DataService<FilingHistory, CompanyRequest> filingHistoryService;
    private final CompanyAuthCodeService companyAuthCodeService;
    private final AppointmentService appointmentService;
    private final DataService<CompanyMetrics, CompanyRequest> companyMetricsService;
    private final CompanyPscStatementServiceImpl companyPscStatementService;
    private final CompanyPscService companyPscService;
    private final RandomService randomService;
    private final DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    private final DataService<Disqualifications, CompanyRequest> disqualificationsService;
    private final CompanyWithPopulatedStructureService companyWithPopulatedStructureService;
    private final CompanySearchService companySearchService;
    private final CompanySearchService alphabeticalCompanySearch;
    private final CompanySearchService advancedCompanySearch;
    private final CompanyDeletionOrchestratorService companyDeletionOrchestratorService;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${elastic.search.deployed}")
    private boolean isElasticSearchDeployed;

    void setAPIUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    void setElasticSearchDeployed(Boolean isElasticSearchDeployed) {
        this.isElasticSearchDeployed = isElasticSearchDeployed;
    }

    public CompanyCreationOrchestratorServiceImpl(
            CompanyProfileService companyProfileService,
            DataService<FilingHistory, CompanyRequest> filingHistoryService,
            CompanyAuthCodeService companyAuthCodeService,
            AppointmentService appointmentService,
            DataService<CompanyMetrics, CompanyRequest> companyMetricsService,
            CompanyPscStatementServiceImpl companyPscStatementService,
            CompanyPscService companyPscService,
            RandomService randomService,
            DataService<CompanyRegisters, CompanyRequest> companyRegistersService,
            DataService<Disqualifications, CompanyRequest> disqualificationsService,
            CompanyWithPopulatedStructureService companyWithPopulatedStructureService,
            @Qualifier("companySearchService") CompanySearchService companySearchService,
            @Qualifier("alphabeticalCompanySearchService")
            CompanySearchService alphabeticalCompanySearch,
            @Qualifier("advancedCompanySearchService") CompanySearchService advancedCompanySearch,
            CompanyDeletionOrchestratorService companyDeletionOrchestratorService) {
        this.companyProfileService = companyProfileService;
        this.filingHistoryService = filingHistoryService;
        this.companyAuthCodeService = companyAuthCodeService;
        this.appointmentService = appointmentService;
        this.companyMetricsService = companyMetricsService;
        this.companyPscStatementService = companyPscStatementService;
        this.companyPscService = companyPscService;
        this.randomService = randomService;
        this.companyRegistersService = companyRegistersService;
        this.disqualificationsService = disqualificationsService;
        this.companyWithPopulatedStructureService = companyWithPopulatedStructureService;
        this.companySearchService = companySearchService;
        this.alphabeticalCompanySearch = alphabeticalCompanySearch;
        this.advancedCompanySearch = advancedCompanySearch;
        this.companyDeletionOrchestratorService = companyDeletionOrchestratorService;
    }

    @Override
    public CompanyProfileResponse createPublicCompany(PublicCompanyRequest companySpec)
            throws DataException {
        var request = mapPublicCompanyToCompanyRequest(companySpec);
        return createCompany(request);
    }

    @Override
    public CompanyProfileResponse createInternalCompany(CompanyRequest companySpec)
            throws DataException {
        return createCompany(companySpec);
    }

    @Override
    public PopulatedCompanyDetailsResponse buildCompanyDataStructure(
            CompanyRequest spec) throws DataException {
        assignCompanyNumber(spec);

        try {
            var response = new PopulatedCompanyDetailsResponse();
            spec.setCompanyWithPopulatedStructureOnly(true);

            var companyProfile = companyProfileService.create(spec);
            LOG.info("Successfully get company profile");
            response.setCompanyProfile(companyProfile);

            var filingHistory = filingHistoryService.create(spec);
            LOG.info("Successfully get filing history");
            response.setFilingHistory(filingHistory);

            if (spec.getNoDefaultOfficer() == null || !spec.getNoDefaultOfficer()) {
                var appointments = appointmentService.createAppointments(spec);
                LOG.info("Successfully get appointments ");
                response.setAppointmentsData(appointments);
            }

            var authCode = companyAuthCodeService.create(spec);
            LOG.info("Successfully get auth code", singleEntryData("auth_code", authCode.getAuthCode()));
            response.setCompanyAuthCode(authCode);

            var companyMetrics = companyMetricsService.create(spec);
            LOG.info("Successfully get company metrics");
            response.setCompanyMetrics(companyMetrics);

            List<CompanyPscStatement> companyPscStatements =
                    companyPscStatementService.createPscStatements(spec);
            LOG.info("Successfully get all PSC statements based on spec counts.");
            response.setCompanyPscStatement(companyPscStatements);

            var companyPscs = companyPscService.create(spec);
            LOG.info("Successfully get PSCs");
            response.setCompanyPscs(companyPscs);

            if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
                var companyRegisters = companyRegistersService.create(spec);
                LOG.info("Successfully get company registers");
                response.setCompanyRegisters(companyRegisters);
            }

            if (spec.getDisqualifiedOfficers() != null && !spec.getDisqualifiedOfficers().isEmpty()) {
                var disqualifications = disqualificationsService.create(spec);
                LOG.info("Successfully get disqualifications");
                response.setDisqualifications(disqualifications);
            }

            return response;
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", spec.getCompanyNumber());
            data.put("error message", ex.getMessage());
            LOG.error("Failed to get company data for company number: " + spec.getCompanyNumber(), ex, data);
            throw new DataException("Failed to get company data from service", ex);
        }
    }

    @Override
    public CompanyProfileResponse createCompanyWithStructure(
            CompanyWithPopulatedStructureRequest companySpec) throws DataException {
        var companyNumber = companySpec.getCompanyProfile().getCompanyNumber();
        var authCode = companySpec.getCompanyAuthCode().getAuthCode();
        companyWithPopulatedStructureService.createCompanyWithPopulatedStructure(companySpec);
        String companyUri = this.apiUrl + "/company/" + companyNumber;
        return new CompanyProfileResponse(companyNumber, authCode, companyUri);
    }

    private CompanyRequest mapPublicCompanyToCompanyRequest(PublicCompanyRequest companySpec) {
        var request = new CompanyRequest();
        request.setJurisdiction(companySpec.getJurisdiction());
        request.setCompanyType(companySpec.getCompanyType());
        request.setCompanyStatus(companySpec.getCompanyStatus());
        request.setSubType(companySpec.getSubType());
        request.setHasSuperSecurePscs(companySpec.getHasSuperSecurePscs());
        if (companySpec.getNumberOfAppointments() != null) {
            request.setNumberOfAppointments(companySpec.getNumberOfAppointments());
        }
        request.setSecureOfficer(companySpec.getSecureOfficer());
        request.setRegisters(companySpec.getRegisters());
        request.setCompanyStatusDetail(companySpec.getCompanyStatusDetail());
        request.setFilingHistoryList(companySpec.getFilingHistoryList());
        if (!CollectionUtils.isEmpty(companySpec.getFilingHistoryList())) {
            request.getFilingHistoryList().forEach(filing -> filing.setDocumentMetadata(false));
        }
        request.setNumberOfAppointments(companySpec.getNumberOfAppointments());
        request.setOfficerRoles(companySpec.getOfficerRoles());
        request.setAccountsDueStatus(companySpec.getAccountsDueStatus());
        request.setNumberOfPscs(companySpec.getNumberOfPscs());
        request.setPscType(companySpec.getPscType());
        request.setPscActive(companySpec.getPscActive());
        request.setWithdrawnStatements(companySpec.getWithdrawnStatements());
        request.setActiveStatements(companySpec.getActiveStatements());
        request.setHasUkEstablishment(companySpec.getHasUkEstablishment());
        request.setRegisteredOfficeIsInDispute(companySpec.getRegisteredOfficeIsInDispute());
        request.setUndeliverableRegisteredOfficeAddress(
                companySpec.getUndeliverableRegisteredOfficeAddress());
        if (companySpec.getForeignCompanyLegalForm() != null
                && !companySpec.getForeignCompanyLegalForm().isBlank()) {
            request.setForeignCompanyLegalForm(companySpec.getForeignCompanyLegalForm());
        }
        return request;
    }

    /**
     * Shared orchestration flow used by both public and internal company creation paths.
     * If any creation step fails, partial company data is rolled back via {@link #handleCreateFailure}.
     */
    protected CompanyProfileResponse createCompany(CompanyRequest companySpec) throws DataException {
        assignCompanyNumber(companySpec);
        companySpec.setCompanyWithPopulatedStructureOnly(false);

        try {
            companyProfileService.create(companySpec);
            LOG.info("Successfully created company profile");

            filingHistoryService.create(companySpec);
            LOG.info("Successfully created filing history");

            if (companySpec.getNoDefaultOfficer() == null || !companySpec.getNoDefaultOfficer()) {
                appointmentService.createAppointments(companySpec);
                LOG.info("Successfully created appointments ");
            }

            var authCode = companyAuthCodeService.create(companySpec);
            LOG.info("Successfully created auth code", singleEntryData("auth_code", authCode.getAuthCode()));

            companyMetricsService.create(companySpec);
            LOG.info("Successfully created company metrics");

            companyPscStatementService.createPscStatements(companySpec);
            LOG.info("Successfully created all PSC statements based on spec counts.");

            companyPscService.create(companySpec);
            LOG.info("Successfully created PSCs");

            if (companySpec.getRegisters() != null && !companySpec.getRegisters().isEmpty()) {
                LOG.info("Creating company registers for company",
                        singleEntryData("company_number", companySpec.getCompanyNumber()));
                companyRegistersService.create(companySpec);
                LOG.info("Successfully created company registers");
            }

            if (companySpec.getDisqualifiedOfficers() != null
                    && !companySpec.getDisqualifiedOfficers().isEmpty()) {
                disqualificationsService.create(companySpec);
                LOG.info("Successfully created disqualifications");
            }

            var companyData = buildCompanyResponse(companySpec, authCode.getAuthCode());
            addCompanyToElasticSearchIndexes(companySpec, companyData);
            LOG.info("Successfully created all company data",
                    singleEntryData("company_number", companySpec.getCompanyNumber()));
            return companyData;
        } catch (Exception ex) {
            throw handleCreateFailure(companySpec.getCompanyNumber(), ex);
        }
    }

    private void assignCompanyNumber(CompanyRequest spec) {
        if (spec == null) {
            throw new IllegalArgumentException("CompanyRequest can not be null");
        }

        String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        if (spec.getIsPaddingCompanyNumber() != null) {
            companyNumberPrefix = companyNumberPrefix + "000";
        }

        do {
            spec.setCompanyNumber(companyNumberPrefix
                    + randomService.getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));
    }

    private CompanyProfileResponse buildCompanyResponse(CompanyRequest spec, String authCode) {
        String companyUri = this.apiUrl + "/company/" + spec.getCompanyNumber();
        return new CompanyProfileResponse(spec.getCompanyNumber(), authCode, companyUri);
    }

    private DataException handleCreateFailure(String companyNumber, Exception ex) throws DataException{
        Map<String, Object> data = new HashMap<>();
        data.put("company number", companyNumber);
        data.put("error message", ex.getMessage());
        LOG.error("Failed to create company data for company number", ex, data);

        try {
            companyDeletionOrchestratorService.deleteCompany(companyNumber);
        } catch (Exception rollbackException) {
            LOG.error("Rollback delete failed for company number " + companyNumber, rollbackException);
        }
        return new DataException("Failed to create company data in service", ex);
    }

    private void addCompanyToElasticSearchIndexes(CompanyRequest spec,
                                                   CompanyProfileResponse companyData)
            throws DataException, ApiErrorResponseException, URIValidationException {

        if (!isElasticSearchDeployed) {
            LOG.debug("Elasticsearch not deployed; skipping indexing for company "
                    + spec.getCompanyNumber());
            return;
        }

        boolean addAlphabeticalIndex = spec.getAlphabeticalSearch() != null;
        boolean addAdvancedIndex = spec.getAdvancedSearch() != null;

        if (Boolean.TRUE.equals(spec.getAddToCompanyElasticSearchIndex())) {
            LOG.info("Adding company to ElasticSearch index",
                    singleEntryData("company_number", spec.getCompanyNumber()));
            companySearchService.addCompanyIntoElasticSearchIndex(companyData);
        }

        if (addAlphabeticalIndex) {
            LOG.info("Adding company to Alphabetical ElasticSearch index: "
                    + spec.getCompanyNumber());
            alphabeticalCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
        }

        if (addAdvancedIndex) {
            LOG.info("Adding company to Advanced ElasticSearch index",
                    singleEntryData("company_number", spec.getCompanyNumber()));
            advancedCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
        }

        LOG.info("Successfully added company to configured ElasticSearch indexes : "
                + spec.getCompanyNumber());
    }

    private Map<String, Object> singleEntryData(String key, Object value) {
        Map<String, Object> data = new HashMap<>();
        data.put(key, value);
        return data;
    }
}

