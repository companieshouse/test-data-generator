package uk.gov.companieshouse.api.testdata.service.impl.workflow;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
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
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.impl.CompanyPscStatementServiceImpl;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DeleteCompanyWorkflowServiceImpl implements DeleteCompanyWorkflowService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final CompanyProfileService companyProfileService;
    private final DataService<FilingHistory, CompanyRequest> filingHistoryService;
    private final CompanyAuthCodeService companyAuthCodeService;
    private final AppointmentService appointmentService;
    private final CompanyPscStatementServiceImpl companyPscStatementService;
    private final CompanyPscService companyPscService;
    private final DataService<CompanyMetrics, CompanyRequest> companyMetricsService;
    private final CompanyAuthAllowListService companyAuthAllowListService;
    private final DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    private final DataService<Disqualifications, CompanyRequest> disqualificationsService;
    private final CompanySearchService companySearchService;
    private final CompanySearchService alphabeticalCompanySearch;
    private final CompanySearchService advancedCompanySearch;

    @Value("${elastic.search.deployed}")
    private boolean isElasticSearchDeployed;

    void setElasticSearchDeployed(Boolean isElasticSearchDeployed) {
        this.isElasticSearchDeployed = isElasticSearchDeployed;
    }

    public DeleteCompanyWorkflowServiceImpl(
            CompanyProfileService companyProfileService,
            DataService<FilingHistory, CompanyRequest> filingHistoryService,
            CompanyAuthCodeService companyAuthCodeService,
            AppointmentService appointmentService,
            CompanyPscStatementServiceImpl companyPscStatementService,
            CompanyPscService companyPscService,
            DataService<CompanyMetrics, CompanyRequest> companyMetricsService,
            CompanyAuthAllowListService companyAuthAllowListService,
            DataService<CompanyRegisters, CompanyRequest> companyRegistersService,
            DataService<Disqualifications, CompanyRequest> disqualificationsService,
            @Qualifier("companySearchService") CompanySearchService companySearchService,
            @Qualifier("alphabeticalCompanySearchService")
            CompanySearchService alphabeticalCompanySearch,
            @Qualifier("advancedCompanySearchService") CompanySearchService advancedCompanySearch) {
        this.companyProfileService = companyProfileService;
        this.filingHistoryService = filingHistoryService;
        this.companyAuthCodeService = companyAuthCodeService;
        this.appointmentService = appointmentService;
        this.companyPscStatementService = companyPscStatementService;
        this.companyPscService = companyPscService;
        this.companyMetricsService = companyMetricsService;
        this.companyAuthAllowListService = companyAuthAllowListService;
        this.companyRegistersService = companyRegistersService;
        this.disqualificationsService = disqualificationsService;
        this.companySearchService = companySearchService;
        this.alphabeticalCompanySearch = alphabeticalCompanySearch;
        this.advancedCompanySearch = advancedCompanySearch;
    }

    @Override
    public void deleteCompany(String companyNumber) throws DataException, NoDataFoundException {
        LOG.info("Attempting to delete company with company number: " + companyNumber);

        if (!companyProfileService.companyExists(companyNumber)) {
            LOG.info("Company with number " + companyNumber + " does not exist. Nothing to delete.");
            throw new NoDataFoundException("Company with number " + companyNumber + " not found");
        }

        List<Exception> suppressedExceptions = new ArrayList<>();

        deleteUkEstablishmentsIfOverseaCompany(companyNumber, suppressedExceptions);
        deleteCompanyData(companyNumber, suppressedExceptions);

        if (!suppressedExceptions.isEmpty()) {
            LOG.error("Errors occurred while deleting company data for company number: " + companyNumber);

            var errorMessage = new StringBuilder(
                    "Error deleting company data. Details: ");

            for (var i = 0; i < suppressedExceptions.size(); i++) {
                var ex = suppressedExceptions.get(i);

                errorMessage.append(" [")
                        .append(i + 1)
                        .append("] ")
                        .append(ex.getMessage() != null
                                ? ex.getMessage()
                                : "Unknown error");

                if (ex.getCause() != null) {
                    errorMessage.append(" Cause: ")
                            .append(ex.getCause().getMessage() != null
                                    ? ex.getCause().getMessage()
                                    : "Unknown cause");
                }
            }

            var ex = new DataException(errorMessage.toString());
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }

        LOG.info("Company with company number " + companyNumber + " deleted successfully without any errors.");
    }

    private void deleteUkEstablishmentsIfOverseaCompany(
            String companyNumber, List<Exception> suppressedExceptions) {
        try {
            LOG.info("Checking if company number " + companyNumber + " is an oversea company.");
            Optional<CompanyProfile> companyProfile = companyProfileService.getCompanyProfile(companyNumber);
            if (isOverseaCompany(companyProfile)) {
                LOG.info("Company number " + companyNumber
                        + " is an oversea company. Deleting UK establishments.");
                deleteUkEstablishmentsForParent(companyNumber, suppressedExceptions);
            } else {
                LOG.info("Company number " + companyNumber
                        + " is not an oversea company. Skipping UK establishments deletion.");
            }
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
    }

    private boolean isOverseaCompany(Optional<CompanyProfile> companyProfile) {
        boolean isOversea = companyProfile.isPresent()
                && CompanyType.OVERSEA_COMPANY.getValue().equals(companyProfile.get().getType());
        LOG.debug("Is company oversea: " + isOversea);
        return isOversea;
    }

    private void deleteUkEstablishmentsForParent(String companyNumber,
                                                  List<Exception> suppressedExceptions) {
        LOG.info("Fetching UK establishments for parent company number: " + companyNumber);
        List<String> ukEstablishments = companyProfileService.findUkEstablishmentsByParent(companyNumber);
        if (ukEstablishments.isEmpty()) {
            LOG.info("No UK establishments found for company number: " + companyNumber);
            return;
        }

        for (String ukEstablishmentNumber : ukEstablishments) {
            try {
                LOG.info("Deleting UK establishment with company number: " + ukEstablishmentNumber);
                deleteCompanyData(ukEstablishmentNumber, suppressedExceptions);
            } catch (Exception ex) {
                suppressedExceptions.add(ex);
            }
        }
    }

    private void deleteCompanyData(String companyNumber, List<Exception> suppressedExceptions) {
        LOG.info("Deleting company data for company number: " + companyNumber);

        try {
            companyProfileService.delete(companyNumber);
            LOG.info("Deleted company profile for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            filingHistoryService.delete(companyNumber);
            LOG.info("Deleted filing history for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyAuthCodeService.delete(companyNumber);
            LOG.info("Deleted company auth code for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            appointmentService.deleteAllAppointments(companyNumber);
            LOG.info("Deleted appointments for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyPscStatementService.delete(companyNumber);
            LOG.info("Deleted PSC statements for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyPscService.delete(companyNumber);
            LOG.info("Deleted PSCs for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyMetricsService.delete(companyNumber);
            LOG.info("Deleted company metrics for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyAuthAllowListService.delete(companyNumber);
            LOG.info("Deleted company auth allow list for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            companyRegistersService.delete(companyNumber);
            LOG.info("Deleted company registers for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }
        try {
            disqualificationsService.delete(companyNumber);
            LOG.info("Deleted disqualifications for company number: " + companyNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(ex);
        }

        if (isElasticSearchDeployed) {
            try {
                LOG.info("Attempting to delete company from ElasticSearch index for company number: "
                        + companyNumber);
                companySearchService.deleteCompanyFromElasticSearchIndex(companyNumber);
                alphabeticalCompanySearch.deleteCompanyFromElasticSearchIndex(companyNumber);
                advancedCompanySearch.deleteCompanyFromElasticSearchIndex(companyNumber);
                LOG.info("Deleted company from ElasticSearch index for company number: "
                        + companyNumber);
            } catch (Exception ex) {
                LOG.error("Failed to delete company from ElasticSearch index for company number: "
                        + companyNumber, ex);
            }
        }
    }
}


