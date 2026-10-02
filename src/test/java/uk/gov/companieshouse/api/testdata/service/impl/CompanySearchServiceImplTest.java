package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
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
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.company.PrivateCompanyLinksResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.PrivateCompanyProfileGet;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.search.PrivateSearchResourceHandler;
import uk.gov.companieshouse.api.handler.search.company.PrivateCompanySearchHandler;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchDelete;
import uk.gov.companieshouse.api.handler.search.company.request.PrivateCompanySearchUpsert;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;

@ExtendWith(MockitoExtension.class)
class CompanySearchServiceImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String UK_ESTABLISHMENT_NUMBER = "BR789012";
    private static final String URI = "/company-search/companies/%s".formatted(COMPANY_NUMBER);
    private static final String UK_ESTABLISHMENT_URI = "/company-search/companies/%s".formatted(UK_ESTABLISHMENT_NUMBER);
    private static final String OVERSEA_COMPANY_TYPE = "oversea-company";
    private static final String COMPANY_NAME = "COMPANY 12345678 LTD";

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
    private ApiResponse<uk.gov.companieshouse.api.company.CompanyProfile> apiResponse;
    @Mock
    private CompanyProfileService companyProfileService;
    @Mock
    private Data companyProfileData;
    @Mock
    uk.gov.companieshouse.api.company.CompanyProfile companyProfile;
    @Mock
    private PrivateCompanyLinksResourceHandler privateCompanyLinksResourceHandler;
    @Mock
    private PrivateCompanyProfileGet privateCompanyProfileGet;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private CompanySearchServiceImpl service;

    @BeforeEach
    void setUp() throws ApiErrorResponseException, URIValidationException {
        // @InjectMocks uses constructor injection, which skips the inherited RestTemplate field
        service.restTemplate = restTemplate;

        Mockito.lenient().when(internalApiClientSupplier.get()).thenReturn(internalApiClient);
        Mockito.lenient().when(internalApiClient.privateCompanyLinksResourceHandler()).thenReturn(privateCompanyLinksResourceHandler);
        Mockito.lenient().when(internalApiClient.privateSearchResourceHandler()).thenReturn(privateSearchResourceHandler);
        Mockito.lenient().when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
        Mockito.lenient().when(privateCompanyLinksResourceHandler.getCompanyProfile(anyString()))
                .thenReturn(privateCompanyProfileGet);
        Mockito.lenient().when(privateCompanyProfileGet.execute()).thenReturn(apiResponse);
        Mockito.lenient().when(apiResponse.getData()).thenReturn(companyProfile);
        Mockito.lenient().when(companyProfile.getData()).thenReturn(companyProfileData);
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
    void addCompanyIntoElasticSearchIndex_ShouldAddUkEstablishmentsForOverseaCompany() throws Exception {
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType(OVERSEA_COMPANY_TYPE);
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER);

        when(companyProfileData.getType()).thenReturn(OVERSEA_COMPANY_TYPE);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(ukEstablishments);

        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, companyProfileData);
        verify(privateCompanySearchHandler).upsertCompanyProfile(UK_ESTABLISHMENT_URI, companyProfileData);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldNotAddUkEstablishmentsForNonOverseaCompany() throws Exception {
        CompanyProfile nonOverseaCompany = new CompanyProfile();
        nonOverseaCompany.setType("ltd");

        when(companyProfileData.getType()).thenReturn("ltd");
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, companyProfileData);
        verify(companyProfileService, never()).findUkEstablishmentsByParent(anyString());
    }

    @Test
    void deleteUkEstablishmentsForOverseaCompanyFromElasticSearchIndex() throws Exception {
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType(OVERSEA_COMPANY_TYPE);
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER);

        stubCompanyFoundInSearch();
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(ukEstablishments);
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
        verify(privateCompanySearchHandler).deleteCompanyProfile(UK_ESTABLISHMENT_URI);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldHandleEmptyEstablishmentsList() throws Exception {
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType(OVERSEA_COMPANY_TYPE);

        when(companyProfileData.getType()).thenReturn(OVERSEA_COMPANY_TYPE);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);

        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());

        CompanyProfileResponse companyData = new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, companyProfileData);
        verify(privateCompanySearchHandler, times(1)).upsertCompanyProfile(anyString(), any());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldHandleEmptyEstablishmentsList() throws Exception {
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType(OVERSEA_COMPANY_TYPE);

        stubCompanyFoundInSearch();
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
        verify(privateCompanySearchHandler, times(1)).deleteCompanyProfile(anyString());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldSkipDelete_WhenCompanyNotInSearchIndex() throws Exception {
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(new LinkedHashMap<String, Object>(), HttpStatus.OK));

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateCompanySearchHandler, never()).deleteCompanyProfile(anyString());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDelete_WhenCompanyOnlyInItems() throws Exception {
        // /search/companies responses carry no top_hit, only an items array
        var item = new LinkedHashMap<String, Object>();
        item.put("company_number", COMPANY_NUMBER);
        var body = new LinkedHashMap<String, Object>();
        body.put("items", List.of(item));
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity<>(body, HttpStatus.OK));
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME);

        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldWrapApiErrorsInDataException() throws Exception {
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute())
                .thenThrow(ApiErrorResponseException.fromIOException(new java.io.IOException("boom")));

        CompanyProfileResponse companyData =
                new CompanyProfileResponse(COMPANY_NUMBER, "authCode", "companyUri");

        DataException ex = assertThrows(DataException.class,
                () -> service.addCompanyIntoElasticSearchIndex(companyData));
        assertTrue(ex.getMessage().contains("Failed to upsert company profile"));
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldSwallowApiErrors() throws Exception {
        stubCompanyFoundInSearch();
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute())
                .thenThrow(ApiErrorResponseException.fromIOException(new java.io.IOException("boom")));

        assertDoesNotThrow(
                () -> service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER, COMPANY_NAME));

        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
    }
}
