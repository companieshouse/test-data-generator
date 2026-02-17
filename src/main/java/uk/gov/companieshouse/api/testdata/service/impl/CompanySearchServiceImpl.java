package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service("companySearchService")
public class CompanySearchServiceImpl implements CompanySearchService {

    private static final String COMPANY_SEARCH_URI = "/company-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s/links";
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
            throws DataException {
        String companyNumber = data.getCompanyNumber();
        String formattedCompanySearchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        try {
            var companyProfileData = fetchCompanyProfile(formattedCompanyProfileUri, companyNumber);
            upsertCompanyProfile(formattedCompanySearchUri, companyProfileData, companyNumber);
            if (OVERSEA_COMPANY_TYPE.equals(companyProfileData.getType())) {
                handleUkEstablishments(companyNumber, true);
            }

        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to upsert company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException {
        try {
            handleUkEstablishments(companyNumber, false);

            String formattedUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
            deleteCompanyProfile(formattedUri, companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to delete company profile for company number: " + companyNumber, ex);
            throw new DataException("Failed to delete company profile: " + ex.getMessage(), ex);
        }
    }

    private void handleUkEstablishments(String companyNumber, boolean isAddOperation)
            throws ApiErrorResponseException, URIValidationException {

        List<String> ukEstablishments =
                companyProfileService.findUkEstablishmentsByParent(companyNumber);
        if (ukEstablishments.isEmpty()) {
            LOG.info("No UK establishments found for oversea company number: " + companyNumber);
        } else {
            LOG.info("Found " + ukEstablishments.size()
                    + " UK establishments for oversea company number: " + companyNumber);
            for (String establishmentNumber : ukEstablishments) {
                if (isAddOperation) {
                    addSingleCompanyToIndex(establishmentNumber);
                } else {
                    deleteSingleCompanyFromIndex(establishmentNumber);
                }
            }
        }
    }

    private void addSingleCompanyToIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        String profileUri = formatUri(COMPANY_PROFILE_URI, companyNumber);

        var profileData = fetchCompanyProfile(profileUri, companyNumber);

        upsertCompanyProfile(searchUri, profileData, companyNumber);
    }

    private void deleteSingleCompanyFromIndex(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String searchUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        deleteCompanyProfile(searchUri, companyNumber);
    }

    private void upsertCompanyProfile(String uri, Data profileData, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Upserting company profile into ElasticSearch for company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .upsertCompanyProfile(uri, profileData)
                .execute();
        LOG.info("Company profile upsert successful for company number: " + companyNumber);
    }

    private void deleteCompanyProfile(String uri, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Deleting company profile from ElasticSearch for company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .companySearch()
                .deleteCompanyProfile(uri)
                .execute();
        LOG.info("Company profile deleted successfully for company number: " + companyNumber);
    }

    private Data fetchCompanyProfile(String uri, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Fetching company profile for company number: " + companyNumber);
        var responseData  = internalApiClientSupplier.get()
                .privateCompanyLinksResourceHandler()
                .getCompanyProfile(uri)
                .execute()
                .getData().getData();
        LOG.info("Company profile fetched successfully for company number: "
                + responseData.getCompanyNumber());
        return responseData;
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}