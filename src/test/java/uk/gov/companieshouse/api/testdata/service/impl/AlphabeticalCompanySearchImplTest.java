package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertTrue;
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
import org.mockito.ArgumentCaptor;
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
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;

@ExtendWith(MockitoExtension.class)
class AlphabeticalCompanySearchImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String COMPANY_NAME = "COMPANY 12345678 LTD";
    private static final String URI = "/alphabetical-search/companies/%s".formatted(COMPANY_NUMBER);
    private static final String GREEN_INSTANCE = "/green";
    private static final String GREEN_URI = "/green/alphabetical-search/companies/%s".formatted(COMPANY_NUMBER);

    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;
    @Mock
    private InternalApiClient internalApiClient;
    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;
    @Mock
    private PrivateAlphabeticalCompanySearchHandler privateAlphabeticalCompanySearchHandler;
    @Mock
    private PrivateAlphabeticalCompanySearchUpsert privateAlphabeticalCompanySearchUpsert;
    @Mock
    private PrivateAlphabeticalCompanySearchDelete privateAlphabeticalCompanySearchDelete;
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

    private AlphabeticalCompanySearchImpl service;

    @BeforeEach
    void setUp() {
        service = new AlphabeticalCompanySearchImpl(internalApiClientSupplier, "");
        service.restTemplate = restTemplate;
        Mockito.lenient().when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        Mockito.lenient().when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        Mockito.lenient().when(privateSearchResourceHandler.alphabeticalCompanySearch())
                .thenReturn(privateAlphabeticalCompanySearchHandler);
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
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(anyString())).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(companyProfileApi);
        when(privateAlphabeticalCompanySearchHandler.put(anyString(), any()))
                .thenReturn(privateAlphabeticalCompanySearchUpsert);
        when(privateAlphabeticalCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateAlphabeticalCompanySearchHandler).put(URI, companyProfileApi);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDeleteCompanyProfile() throws Exception {
        stubCompanyFoundInSearch();
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldSkipDelete_WhenCompanyNotInSearchIndex() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(new LinkedHashMap<String, Object>(), HttpStatus.OK));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAlphabeticalCompanySearchHandler, never()).delete(anyString());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenApiErrorResponseExceptionThrown()
            throws Exception {
        stubCompanyFoundInSearch();
        // Mock the delete method to throw ApiErrorResponseException
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute())
                .thenThrow(new ApiErrorResponseException(new HttpResponseException.Builder(500,
                        "API error", new HttpHeaders())));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
        // Verify that the error is logged
        verify(privateAlphabeticalCompanySearchDelete).execute();
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldLogError_WhenUriValidationExceptionThrown()
            throws Exception {
        stubCompanyFoundInSearch();
        // Mock the delete method to throw URIValidationException
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute())
                .thenThrow(new URIValidationException("URI validation error"));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
        // Verify that the error is logged
        verify(privateAlphabeticalCompanySearchDelete).execute();
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldUpsertWithGreenPrefix() throws Exception {
        service = new AlphabeticalCompanySearchImpl(internalApiClientSupplier, GREEN_INSTANCE);
        service.restTemplate = restTemplate;
        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(anyString())).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);
        when(apiResponse.getData()).thenReturn(companyProfileApi);
        when(privateAlphabeticalCompanySearchHandler.put(anyString(), any()))
                .thenReturn(privateAlphabeticalCompanySearchUpsert);
        when(privateAlphabeticalCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateAlphabeticalCompanySearchHandler).put(GREEN_URI, companyProfileApi);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldAttemptDelete_WhenCompanyNameUnknown()
            throws Exception {
        // Without a name the alphabetical index cannot be queried, so the delete must still be tried
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, null);

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
        verify(restTemplate, never())
                .exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldAttemptDelete_WhenCompanyNameBlank()
            throws Exception {
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, "   ");

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
        verify(restTemplate, never())
                .exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDeleteWithGreenPrefix() throws Exception {
        service = new AlphabeticalCompanySearchImpl(internalApiClientSupplier, GREEN_INSTANCE);
        service.restTemplate = restTemplate;
        stubCompanyFoundInSearch();
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateAlphabeticalCompanySearchHandler).delete(GREEN_URI);

        ArgumentCaptor<String> searchUrl = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(searchUrl.capture(), eq(HttpMethod.GET), any(),
                eq(Map.class));
        assertTrue(searchUrl.getValue().contains(GREEN_INSTANCE + "/alphabetical-search/companies?q="),
                "green instance should query its own alphabetical index, but was: "
                        + searchUrl.getValue());
    }
}