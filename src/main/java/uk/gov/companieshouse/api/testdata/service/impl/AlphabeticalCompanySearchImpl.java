package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.function.Supplier;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public class AlphabeticalCompanySearchImpl implements CompanySearchService {
    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private final String instance;
    private static final String ALPHABETICAL_SEARCH_URI = "%s/alphabetical-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AlphabeticalCompanySearchImpl.class));

    public AlphabeticalCompanySearchImpl(Supplier<InternalApiClient> internalApiClientSupplier, String instance) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.instance = instance;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyProfileResponse data) throws
            ApiErrorResponseException, URIValidationException {
        String companyNumber = data.getCompanyNumber();
        var formattedAlphabeticalSearchUri = String.format(ALPHABETICAL_SEARCH_URI, instance,
                companyNumber);
        LOG.info("Adding company into " + instance + " alphabetical search index for company number: " + companyNumber);
        var companyProfileApi = getCompanyProfile(companyNumber);
        upsertCompanyProfileForAlphaSearch(
                formattedAlphabeticalSearchUri, companyProfileApi, companyNumber);

    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) {
        var uri = String.format(ALPHABETICAL_SEARCH_URI, instance,
                companyNumber);
        LOG.info("Deleting company profile from " + instance + " alphabetical search for company number: " + companyNumber);
        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .alphabeticalCompanySearch()
                    .delete(uri)
                    .execute();
            LOG.info("Company profile deleted successfully from " + instance + " alphabetical search for company number: "
                    + companyNumber);
        } catch (ApiErrorResponseException | URIValidationException ex) {
            LOG.error("Failed to delete company profile from " + instance + " alphabetical search "
                    + "for company number: " + companyNumber);
        }
    }

    private void upsertCompanyProfileForAlphaSearch(
            String uri, CompanyProfileApi profileData, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Upserting company for " + instance + " alphabetical search with company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .alphabeticalCompanySearch()
                .put(uri, profileData)
                .execute();
        LOG.info("Company profile upsert into " + instance + " alphabetical search is successful for company number:"
                + companyNumber);
    }

    CompanyProfileApi getCompanyProfile(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        var uri = String.format(COMPANY_PROFILE_URI, companyNumber);
        var companyProfileApiResponse
                = internalApiClientSupplier.get().company().get(uri).execute();
        return companyProfileApiResponse.getData();
    }
}
