package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;

@Service
public class CompanySearchServiceImpl implements CompanySearchService {

    private static final String COMPANY_SEARCH_URI = "/company-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";

    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final Logger logger;

    public CompanySearchServiceImpl(
            Supplier<InternalApiClient> internalApiClientSupplier, Logger logger) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.logger = logger;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data)
            throws DataException, ApiErrorResponseException, URIValidationException {
        String formattedCompanySearchUri = formatUri(COMPANY_SEARCH_URI, data.getCompanyNumber());
        String formattedCompanyProfileUri = formatUri(COMPANY_PROFILE_URI, data.getCompanyNumber());
        var companyProfileData = internalApiClientSupplier.get()
                .privateCompanyResourceHandler()
                .getCompanyFullProfile(formattedCompanyProfileUri).execute().getData();

        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .upsertCompanyProfile(formattedCompanySearchUri, companyProfileData)
                    .execute();
            logger.info(
                    "Company profile upsert is successful with company number: "
                            + data.getCompanyNumber());
        } catch (ApiErrorResponseException ex) {
            logger.error("API error occurred while upserting company profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        } catch (URIValidationException ex) {
            logger.error(
                    "URI validation error occurred while upserting profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException("Failed to upsert company profile: " + ex.getMessage(), ex);
        }
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException {
        String formattedUri = formatUri(COMPANY_SEARCH_URI, companyNumber);
        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .deleteCompanyProfile(formattedUri)
                    .execute();
        } catch (ApiErrorResponseException | URIValidationException ex) {
            throw new DataException("Failed to upsert company profile: " + ex.getMessage());
        }
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}
