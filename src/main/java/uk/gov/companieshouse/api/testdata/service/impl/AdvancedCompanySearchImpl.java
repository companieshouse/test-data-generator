package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service("advancedCompanySearchService")
public class AdvancedCompanySearchImpl implements CompanySearchService {

    private static final String ADVANCED_SEARCH_URI = "/advanced-search/companies/%s";
    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private static final String COMPANY_PROFILE_URI = "/company/%s";

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AdvancedCompanySearchImpl.class));

    public AdvancedCompanySearchImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        this.internalApiClientSupplier = internalApiClientSupplier;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data)
            throws ApiErrorResponseException, URIValidationException {
        String companyNumber = data.getCompanyNumber();
        String formattedAdvancedSearchUri = formatUri(ADVANCED_SEARCH_URI, companyNumber);
        LOG.info("Adding company into advanced search index for company number: "
                + companyNumber);
        var companyProfileApi = getCompanyProfile(companyNumber);
        upsertCompanyProfileForAdvancedSearch(
                formattedAdvancedSearchUri, companyProfileApi, companyNumber);
    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) {
        deleteCompanyFromAdvancedSearch(companyNumber);
    }

    private void upsertCompanyProfileForAdvancedSearch(
            String uri, CompanyProfileApi profileData, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Upserting company for advanced search with company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .advancedCompanySearch()
                .upsertCompanyProfile(uri, profileData)
                .execute();
        LOG.info("Company profile upsert into advanced search is successful for company number:"
                + companyNumber);
    }

    private void deleteCompanyFromAdvancedSearch(String companyNumber) {
        String uri = formatUri(ADVANCED_SEARCH_URI, companyNumber);
        LOG.info("Deleting company profile from advanced search for company number: "
                + companyNumber);
        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .advancedCompanySearch()
                    .deleteCompanyProfile(uri)
                    .execute();
            LOG.info("Company profile deleted successfully from advanced search "
                    + "for company number: " + companyNumber);

        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to delete company profile from advanced search for company number: "
                    + companyNumber);
        }
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }

    CompanyProfileApi getCompanyProfile(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String uri = formatUri(COMPANY_PROFILE_URI, companyNumber);
        var companyProfileApiResponse
                = internalApiClientSupplier.get().company().get(uri).execute();
        return companyProfileApiResponse.getData();
    }
}
