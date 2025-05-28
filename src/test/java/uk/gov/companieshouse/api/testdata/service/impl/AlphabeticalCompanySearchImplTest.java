package uk.gov.companieshouse.api.testdata.service.impl;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.handler.company.CompanyResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.CompanyGet;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.PrivateAlphabeticalCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.alphabeticalCompany.request.PrivateAlphabeticalCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;

@ExtendWith(MockitoExtension.class)
class AlphabeticalCompanySearchImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String URI = "/alphabetical-search/companies/%s".formatted(COMPANY_NUMBER);

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

    @InjectMocks
    private AlphabeticalCompanySearchImpl service;

    @BeforeEach
    void setUp() {
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        when(internalApiClient.privateSearchResourceHandler())
                .thenReturn(privateSearchResourceHandler);
        when(privateSearchResourceHandler.alphabeticalCompanySearch())
                .thenReturn(privateAlphabeticalCompanySearchHandler);
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

        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateAlphabeticalCompanySearchHandler).put(URI, companyProfileApi);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDeleteCompanyProfile() throws Exception {
        when(privateAlphabeticalCompanySearchHandler.delete(anyString()))
                .thenReturn(privateAlphabeticalCompanySearchDelete);
        when(privateAlphabeticalCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        verify(privateAlphabeticalCompanySearchHandler).delete(URI);
    }
}