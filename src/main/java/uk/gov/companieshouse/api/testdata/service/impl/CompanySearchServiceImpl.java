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
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class CompanySearchServiceImpl implements CompanySearchService {

    private static final String COMPANY_SEARCH_URI = "/company-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";
    private final Supplier<InternalApiClient> internalApiClientSupplier;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanySearchServiceImpl.class));

    public CompanySearchServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        this.internalApiClientSupplier = internalApiClientSupplier;
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
            LOG.info("Company profile upsert is successful with company number: "
                    + data.getCompanyNumber());
        } catch (ApiErrorResponseException | URIValidationException ex) {
            throw new DataException("Failed to upsert company profile: " + ex.getMessage());
        }
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException {
        String formattedUri = formatUri(COMPANY_SEARCH_URI, companyNumber);

        Map<String, Object> logData = new HashMap<>();
        logData.put("company_number", companyNumber);
        logData.put("elasticsearch_uri", formattedUri);

        try {
            LOG.info("Attempting to delete company from ElasticSearch index", logData);

            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .deleteCompanyProfile(formattedUri)
                    .execute();

            LOG.info("Successfully deleted company from ElasticSearch index", logData);

        } catch (ApiErrorResponseException ex) {
            logData.put("error_type", "ApiErrorResponseException");
            logData.put("status_code", ex.getStatusCode());
            logData.put("error_message", ex.getMessage());
            LOG.error("Failed to delete company from ElasticSearch index - API error response",
                    ex, logData);
            throw new DataException("Failed to delete company profile due to API error: "
                    + ex.getMessage(), ex);

        } catch (URIValidationException ex) {
            logData.put("error_type", "URIValidationException");
            logData.put("error_message", ex.getMessage());
            LOG.error("Failed to delete company from ElasticSearch index - URI validation error",
                    ex, logData);
            throw new DataException("Failed to delete company profile due to invalid URI: "
                    + ex.getMessage(), ex);

        } catch (Exception ex) {
            logData.put("error_type", "UnexpectedException");
            logData.put("error_message", ex.getMessage());
            LOG.error("Failed to delete company from ElasticSearch index - unexpected error",
                    ex, logData);
            throw new DataException("Failed to delete company profile due to unexpected error: "
                    + ex.getMessage(), ex);
        }
    }


    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}
