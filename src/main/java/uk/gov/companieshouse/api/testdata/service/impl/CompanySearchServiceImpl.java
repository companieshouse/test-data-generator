package uk.gov.companieshouse.api.testdata.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
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

    @Autowired
    private final ObjectMapper objectMapper;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanyProfileServiceImpl.class));

    public CompanySearchServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier,
                                    ObjectMapper objectMapper) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data)
            throws DataException, ApiErrorResponseException, URIValidationException {
        String formattedUri = formatUri(COMPANY_SEARCH_URI, data.getCompanyNumber());
        CompanyProfileApi companyProfileApi = getCompanyProfile(data.getCompanyNumber());
        String companyProfileJson = serializeCompanyProfile(companyProfileApi);

        Data companyProfileData = deserializeCompanyProfile(companyProfileJson);
        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .upsertCompanyProfile(formattedUri, companyProfileData)
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

    private CompanyProfileApi getCompanyProfile(String companyNumber)
            throws ApiErrorResponseException, URIValidationException {
        String uri = formatUri(COMPANY_PROFILE_URI, companyNumber);
        var companyProfileApiApiResponse
                = internalApiClientSupplier.get().company().get(uri).execute();
        return companyProfileApiApiResponse.getData();
    }

    private Data deserializeCompanyProfile(String data) throws DataException {
        try {
            return objectMapper.readValue(data, Data.class);
        } catch (JsonProcessingException exception) {
            throw new DataException("Unable to parse message payload data", exception);
        }
    }

    private String serializeCompanyProfile(CompanyProfileApi companyProfileApi) throws DataException {
        try {
            return objectMapper.writeValueAsString(companyProfileApi);
        } catch (JsonProcessingException ex) {
            throw new DataException("Failed to serialize CompanyProfileApi to JSON", ex);
        }
    }

    private String formatUri(String template, String value) {
        return String.format(template, value);
    }
}
