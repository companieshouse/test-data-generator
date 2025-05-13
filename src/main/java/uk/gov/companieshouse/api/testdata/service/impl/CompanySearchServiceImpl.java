package uk.gov.companieshouse.api.testdata.service.impl;

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
    private static final String ERROR_MSG_ADD_COMPANY =
            "Error occurred while adding company into ElasticSearch index: ";
    private static final String ERROR_MSG_DELETE_COMPANY =
            "Error occurred while deleting company from ElasticSearch index: ";
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
            LOG.info(
                    "Company profile upsert is successful with company number: "
                            + data.getCompanyNumber());
        } catch (ApiErrorResponseException ex) {
            LOG.error("API error occurred while upserting company profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException(ERROR_MSG_ADD_COMPANY + ex.getMessage(), ex);
        } catch (URIValidationException ex) {
            LOG.error(
                    "URI validation error occurred while upserting profile for company number: "
                    + data.getCompanyNumber() + ". Error: " + ex.getMessage(), ex);
            throw new DataException(ERROR_MSG_ADD_COMPANY + ex.getMessage(), ex);
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
            throw new DataException(ERROR_MSG_DELETE_COMPANY + ex.getMessage());
        }
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}
