package uk.gov.companieshouse.api.testdata.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.advanced.PrivateAdvancedCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.advanced.request.PrivateAdvancedCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.advanced.request.PrivateAdvancedCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;

@ExtendWith(MockitoExtension.class)
class AdvancedCompanySearchImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String URI = "/advanced-search/companies/%s".formatted(COMPANY_NUMBER);

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;
    @Mock
    private InternalApiClient internalApiClient;
    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;
    @Mock
    private PrivateAdvancedCompanySearchHandler privateAdvancedCompanySearchHandler;
    @Mock
    private PrivateAdvancedCompanySearchUpsert privateAdvancedCompanySearchUpsert;
    @Mock
    private PrivateAdvancedCompanySearchDelete privateAdvancedCompanySearchDelete;
    @Mock
    private ApiResponse<CompanyProfileApi> apiResponse;
    @Mock
    private CompanyProfileApi companyProfileApi;
    @Mock
    private CompanyResourceHandler companyResourceHandler;

    @Mock
    private CompanyGet companyGet;

    @InjectMocks
    private AdvancedCompanySearchImpl service;

    @BeforeEach
    void setUp() {
        // Mock the InternalApiClient supplier
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);

        // Mock the private search resource handler
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.advancedCompanySearch())
                .thenReturn(privateAdvancedCompanySearchHandler);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldUpsertCompanyProfile() throws Exception {
        // Mock the company resource handler
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(anyString())).thenReturn(companyGet);

        // Mock the execute method to return an ApiResponse
        when(companyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(companyProfileApi);
        when(privateAdvancedCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateAdvancedCompanySearchUpsert);
        when(privateAdvancedCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);
        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateAdvancedCompanySearchHandler).upsertCompanyProfile(URI, companyProfileApi);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDeleteCompanyProfile() throws Exception {
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenApiErrorResponseExceptionThrown()
            throws Exception {
        // Mock the deleteCompanyProfile to throw ApiErrorResponseException
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute())
                .thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(500,
                        "API error", new HttpHeaders())));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        // Verify that the error is logged
        verify(privateAdvancedCompanySearchDelete).execute();
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenUriValidationExceptionThrown()
            throws Exception {
        // Mock the deleteCompanyProfile to throw URIValidationException
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute())
                .thenThrow(new URIValidationException("URI validation error"));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        // Verify that the error is logged
        verify(privateAdvancedCompanySearchDelete).execute();
    }
}