package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.company.PrivateCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;

class CompanySearchServiceImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String URI = "/company-search/companies/%s".formatted(COMPANY_NUMBER);

    @Mock
    private CompanyResourceHandler companyResourceHandler;
    @Mock
    private CompanyGet companyGet;
    @Mock
    private Supplier<InternalApiClient> internalApiClientSupplier;
    @Mock
    private InternalApiClient internalApiClient;
    @Mock
    private PrivateSearchResourceHandler privateSearchResourceHandler;
    @Mock
    private PrivateCompanySearchHandler privateCompanySearchHandler;
    @Mock
    private PrivateCompanySearchUpsert privateCompanySearchUpsert;
    @Mock
    private PrivateCompanySearchDelete privateCompanySearchDelete;

    private CompanySearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        service = new CompanySearchServiceImpl(internalApiClientSupplier, objectMapper);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_Success() throws Exception {
        // given
        CompanyProfileApi companyProfileApi = mock(CompanyProfileApi.class);
        ApiResponse<CompanyProfileApi> apiResponse = mock(ApiResponse.class);
        when(apiResponse.getData()).thenReturn(companyProfileApi);

        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(anyString())).thenReturn(companyGet);
        when(companyGet.execute()).thenReturn(apiResponse);

        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");

        // when
        service.addCompanyIntoElasticSearchIndex(companyData);

        // then
        verify(privateCompanySearchHandler).upsertCompanyProfile(anyString(), any());
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldHandleNon200Response() throws Exception {
        // given
        CompanyData companyData = mock(CompanyData.class);
        when(companyData.getCompanyNumber()).thenReturn(COMPANY_NUMBER);

        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenThrow(ApiErrorResponseException.class);

        when(internalApiClient.company()).thenReturn(companyResourceHandler);
        when(companyResourceHandler.get(anyString())).thenReturn(companyGet);
        when(companyGet.execute()).thenThrow(ApiErrorResponseException.class);

        // then
        assertThrows(ApiErrorResponseException.class, () ->
                service.addCompanyIntoElasticSearchIndex(companyData));
    }

    @Test
    void shouldSuccessfullySendDeleteRequest() throws Exception {
        // given
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch())
                .thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.deleteCompanyProfile(URI))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        // when
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        // then
        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldHandleNon200Response() throws Exception {
        // given
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.deleteCompanyProfile(URI))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenThrow(ApiErrorResponseException.class);

        // then
        assertThrows(DataException.class, () ->
                service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER));
    }
}
