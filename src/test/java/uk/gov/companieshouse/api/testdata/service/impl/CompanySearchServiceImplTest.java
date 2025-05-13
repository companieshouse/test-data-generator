package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.PrivateCompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.PrivateCompanyFullProfileGet;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.company.PrivateCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.logging.Logger;

@ExtendWith(MockitoExtension.class)
class CompanySearchServiceImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String URI = "/company-search/companies/%s".formatted(COMPANY_NUMBER);

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
    @Mock
    private ApiResponse<Data> apiResponse;
    @Mock
    private PrivateCompanyResourceHandler privateCompanyResourceHandler;
    @Mock
    private Logger logger;

    private CompanySearchServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        service = new CompanySearchServiceImpl(internalApiClientSupplier, logger);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_Success() throws Exception {
        // given
        PrivateCompanyFullProfileGet privateCompanyFullProfileGet
                = mock(PrivateCompanyFullProfileGet.class);

        // mock the ApiResponse
        // ApiResponse<Data> apiResponse = mock(ApiResponse.class);

        // Mocking the internalApiClient and its handlers
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateCompanyResourceHandler())
                .thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyFullProfile(anyString()))
                .thenReturn(privateCompanyFullProfileGet);
        when(privateCompanyFullProfileGet.execute()).thenReturn(apiResponse);

        // Mocking the search resource handler
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        // when
        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        // then
        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, apiResponse.getData());
        verify(privateCompanySearchUpsert).execute();
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldHandleNon200Response() throws Exception {
        // given
        PrivateCompanyFullProfileGet privateCompanyFullProfileGet
                = mock(PrivateCompanyFullProfileGet.class);

        // Mocking the internalApiClient and its handlers
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateCompanyResourceHandler())
                .thenReturn(privateCompanyResourceHandler);
        when(privateCompanyResourceHandler.getCompanyFullProfile(anyString()))
                .thenReturn(privateCompanyFullProfileGet);
        when(privateCompanyFullProfileGet.execute()).thenReturn(apiResponse);

        // given
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any())).thenReturn(
                privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenThrow(ApiErrorResponseException.class);

        // then
        CompanyData companyData =
                new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        assertThrows(DataException.class, () ->
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