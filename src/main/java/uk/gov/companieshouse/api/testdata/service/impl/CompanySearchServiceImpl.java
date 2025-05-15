package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanySearchServiceImpl implements CompanySearchService {

    private static final String COMPANY_SEARCH_URI = "/company-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final CompanyProfileService companyProfileService;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanySearchServiceImpl.class));

    public CompanySearchServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier,
                                    CompanyProfileService companyProfileService) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.companyProfileService = companyProfileService;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data)
            throws DataException, ApiErrorResponseException, URIValidationException {
        String companyNumber = data.getCompanyNumber();
        String formattedCompanySearchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        try {
            LOG.info("Fetching company profile for company number: " + companyNumber);
            var companyProfileData = internalApiClientSupplier.get()
                    .privateCompanyResourceHandler()
                    .getCompanyFullProfile(formattedCompanyProfileUri)
                    .execute()
                    .getData();

            LOG.info("Upserting company profile into ElasticSearch for company number: "
                    + companyNumber);
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .upsertCompanyProfile(formattedCompanySearchUri, companyProfileData)
                    .execute();
            LOG.info("Company profile upsert successful for company number: " + companyNumber);

            handleUkEstablishmentsForOverseaCompany(companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to upsert company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException {
        try {
            LOG.info("Deleting company profile from ElasticSearch for company number: "
                    + companyNumber);
            handleUkEstablishmentsDeletion(companyNumber);

            String formattedUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .deleteCompanyProfile(formattedUri)
                    .execute();
            LOG.info("Company profile deleted successfully for company number: " + companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to delete company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to delete company profile: " + ex.getMessage(), ex);
        }
    }

    private void handleUkEstablishmentsForOverseaCompany(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Checking if company number " + companyNumber + " is an oversea company.");
        Optional<CompanyProfile> companyProfile =
                companyProfileService.getCompanyProfile(companyNumber);
        if (companyProfile.isPresent()
                && OVERSEA_COMPANY_TYPE.equals(companyProfile.get().getType())) {
            LOG.info("Company number " + companyNumber
                    + " is an oversea company. Fetching UK establishments.");
            List<String> ukEstablishments =
                    companyProfileService.findUkEstablishmentsByParent(companyNumber);
            if (ukEstablishments.isEmpty()) {
                LOG.info("No UK establishments found for oversea company number: " + companyNumber);
            } else {
                for (String establishmentNumber : ukEstablishments) {
                    LOG.info("Adding UK establishment "
                            + establishmentNumber + " to ElasticSearch index.");
                    addSingleCompanyToIndex(establishmentNumber);
                }
            }
        } else {
            LOG.info("Company number " + companyNumber
                    + " is not an oversea company. Skipping UK establishments.");
        }
    }

    private void handleUkEstablishmentsDeletion(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Checking if company number "
                + companyNumber + " is an oversea company for deletion.");
        Optional<CompanyProfile> companyProfile =
                companyProfileService.getCompanyProfile(companyNumber);
        if (companyProfile.isPresent()
                && OVERSEA_COMPANY_TYPE.equals(companyProfile.get().getType())) {
            LOG.info("Company number " + companyNumber
                    + " is an oversea company. Fetching UK establishments for deletion.");
            List<String> ukEstablishments =
                    companyProfileService.findUkEstablishmentsByParent(companyNumber);
            if (ukEstablishments.isEmpty()) {
                LOG.info("No UK establishments found for deletion for oversea company number: "
                        + companyNumber);
            } else {
                for (String establishmentNumber : ukEstablishments) {
                    LOG.info("Deleting UK establishment "
                            + establishmentNumber + " from ElasticSearch index.");
                    deleteSingleCompanyFromIndex(establishmentNumber);
                }
            }
        } else {
            LOG.info("Company number " + companyNumber
                    + " is not an oversea company. Skipping UK establishments deletion.");
        }
    }

    private void addSingleCompanyToIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Adding single company " + companyNumber + " to ElasticSearch index.");
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String profileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        var profileData = internalApiClientSupplier.get()
                .privateCompanyResourceHandler()
                .getCompanyFullProfile(profileUri)
                .execute()
                .getData();

        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .upsertCompanyProfile(searchUri, profileData)
                .execute();
        LOG.info("Company profile upsert successful for company number: " + companyNumber);
    }

    private void deleteSingleCompanyFromIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Deleting single company " + companyNumber + " from ElasticSearch index.");
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .deleteCompanyProfile(searchUri)
                .execute();
        LOG.info("Company profile deleted successfully for company number: " + companyNumber);
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}