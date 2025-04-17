package uk.gov.companieshouse.api.testdata.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.ApiClient;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;

import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;

import java.util.function.Supplier;


@Service
public class CompanySearchServiceImpl implements CompanySearchService {
    @Value("${api.url}")
    private String apiUrl;

    private static final String URI = "/company-search/companies/%s";

    private final Supplier<InternalApiClient> internalApiClientSupplier;

    private final ObjectMapper objectMapper;

    public CompanySearchServiceImpl(Supplier<InternalApiClient> internalApiClientSupplier, ObjectMapper objectMapper) {
        this.internalApiClientSupplier = internalApiClientSupplier;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void addCompanyIntoElasticSearchIndex(CompanyData data) throws DataException, ApiErrorResponseException, URIValidationException {
        final String formattedUri = String.format(URI, data.getCompanyNumber());
        CompanyProfileApi companyProfileApi = getCompanyProfile(data.getCompanyNumber());
        Data companyProfileData = deserialiseCompanyProfile(String.valueOf(companyProfileApi));
        try {
            internalApiClientSupplier.get()
                    .privateSearchResourceHandler()
                    .companySearch()
                    .upsertCompanyProfile(formattedUri, companyProfileData)
                    .execute();
        } catch (ApiErrorResponseException | URIValidationException ex) {
            throw new DataException("Failed to upsert company profile: " + ex.getMessage());
        }
    }

//    @Override
//    public String deleteCompanyFromElasticSearchIndex(String companyNumber) {
//        return "";
//    }

    public CompanyProfileApi getCompanyProfile(String companyNumber)
            throws DataException, ApiErrorResponseException, URIValidationException {
        String uri = String.format("/company/%s", companyNumber);
        var companyProfileApiApiResponse = internalApiClientSupplier.get().company().get(uri).execute();
        return companyProfileApiApiResponse.getData();
    }

    public Data deserialiseCompanyProfile(String data) throws DataException {
        try {
            return objectMapper.readValue(data, Data.class);
        } catch (JsonProcessingException exception) {
            throw new DataException("Unable to parse message payload data", exception);
        }
    }
}
