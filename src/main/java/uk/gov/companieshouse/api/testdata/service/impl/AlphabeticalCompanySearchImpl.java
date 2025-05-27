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

@Service
public class AlphabeticalCompanySearchImpl implements CompanySearchService {
    private final Supplier<InternalApiClient> internalApiClientSupplier;
    private static final String ALPHABETICAL_SEARCH_URI = "/alphabetical-search/companies/%s";
    private static final String COMPANY_PROFILE_URI = "/company/%s";

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(AlphabeticalCompanySearchImpl.class));

    public AlphabeticalCompanySearchImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        this.internalApiClientSupplier = internalApiClientSupplier;
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data) throws
            ApiErrorResponseException, URIValidationException {
        String companyNumber = data.getCompanyNumber();
        String formattedAlphabeticalSearchUri = String.format(ALPHABETICAL_SEARCH_URI,
                companyNumber);
        LOG.info("Adding company into alphabetical search index for company number: "
                + companyNumber);
        var companyProfileApi = getCompanyProfile(companyNumber);
        upsertCompanyProfileForAlphaSearch(
                formattedAlphabeticalSearchUri, companyProfileApi, companyNumber);

    }

    @Override
    public void deleteCompanyFromElasticSearchIndex(String companyNumber) throws
            ApiErrorResponseException, URIValidationException {
        String uri =  String.format(ALPHABETICAL_SEARCH_URI,
                companyNumber);
        LOG.info("Deleting company profile from alphabetical search for company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .alphabeticalCompanySearch()
                .delete(uri)
                .execute();
        LOG.info("Company profile deleted successfully from alphabetical search for company number:"
                + companyNumber);
    }

    private void upsertCompanyProfileForAlphaSearch(
            String uri, CompanyProfileApi profileData, String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        LOG.info("Upserting company for alphabetical search with company number: "
                + companyNumber);
        internalApiClientSupplier.get()
                .privateSearchResourceHandler()
                .alphabeticalCompanySearch()
                .put(uri, profileData)
                .execute();
        LOG.info("Company profile upsert into alphabetical search is successful for company number:"
                + companyNumber);
    }

    CompanyProfileApi getCompanyProfile(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String uri = String.format(COMPANY_PROFILE_URI, companyNumber);
        var companyProfileApiResponse
                = internalApiClientSupplier.get().company().get(uri).execute();
        return companyProfileApiResponse.getData();
    }
}
