package uk.gov.companieshouse.api.testdata.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpResponseException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

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
    private static final String COMPANY_NAME = "COMPANY 12345678 LTD";
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

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AdvancedCompanySearchImpl service;

    @BeforeEach
    void setUp() {
        // @InjectMocks uses constructor injection, which skips the inherited RestTemplate field
        service.restTemplate = restTemplate;

        // Mock the InternalApiClient supplier
        Mockito.lenient().when(internalApiClientSupplier.get()).thenReturn(internalApiClient);

        // Mock the private search resource handler
        Mockito.lenient().when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        Mockito.lenient().when(privateSearchResourceHandler.advancedCompanySearch())
                .thenReturn(privateAdvancedCompanySearchHandler);
    }

    private void stubCompanyFoundInSearch() {
        var topHit = new LinkedHashMap<String, Object>();
        topHit.put("company_name", COMPANY_NAME);
        topHit.put("company_number", COMPANY_NUMBER);
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", topHit);
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
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
        stubCompanyFoundInSearch();
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldSkipDelete_WhenCompanyNotInSearchIndex() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(new LinkedHashMap<String, Object>(), HttpStatus.OK));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAdvancedCompanySearchHandler, never()).deleteCompanyProfile(anyString());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenApiErrorResponseExceptionThrown()
            throws Exception {
        stubCompanyFoundInSearch();
        // Mock the deleteCompanyProfile to throw ApiErrorResponseException
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute())
                .thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(500,
                        "API error", new HttpHeaders())));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        // Verify that the error is logged
        verify(privateAdvancedCompanySearchDelete).execute();
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenUriValidationExceptionThrown()
            throws Exception {
        stubCompanyFoundInSearch();
        // Mock the deleteCompanyProfile to throw URIValidationException
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute())
                .thenThrow(new URIValidationException("URI validation error"));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        // Verify that the error is logged
        verify(privateAdvancedCompanySearchDelete).execute();
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldAttemptDelete_WhenCompanyNameUnknown()
            throws Exception {
        // Without a name the advanced index cannot be queried, so the delete must still be tried
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, null);

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        verify(restTemplate, never())
                .exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldAttemptDelete_WhenCompanyNameBlank()
            throws Exception {
        when(privateAdvancedCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateAdvancedCompanySearchDelete);
        when(privateAdvancedCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, "   ");

        verify(privateAdvancedCompanySearchHandler).deleteCompanyProfile(URI);
        verify(restTemplate, never())
                .exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }
}