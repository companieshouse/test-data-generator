package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Mockito;
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
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;

@ExtendWith(MockitoExtension.class)
class CompanySearchServiceImplTest {

    private static final ApiResponse<Void> SUCCESS_RESPONSE = new ApiResponse<>(200, null);
    private static final String COMPANY_NUMBER = "12345678";
    private static final String UK_ESTABLISHMENT_NUMBER = "BR789012";
    private static final String URI = "/company-search/companies/%s".formatted(COMPANY_NUMBER);
    private static final String UK_ESTABLISHMENT_URI = "/company-search/companies/%s".formatted(UK_ESTABLISHMENT_NUMBER);

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
    private PrivateCompanyFullProfileGet privateCompanyFullProfileGet;
    @Mock
    private CompanyProfileService companyProfileService;

    @InjectMocks
    private PrivateCompanyResourceHandler privateCompanyResourceHandler;
    @Mock
    private Logger logger;

    private CompanySearchServiceImpl service;

    @BeforeEach
    void setUp() {
        when(internalApiClientSupplier.get()).thenReturn(internalApiClient);

        // Marking stubbings as lenient to avoid UnnecessaryStubbingException
        Mockito.lenient().when(internalApiClient.privateCompanyResourceHandler()).thenReturn(privateCompanyResourceHandler);
        Mockito.lenient().when(internalApiClient.privateSearchResourceHandler()).thenReturn(privateSearchResourceHandler);
        Mockito.lenient().when(privateSearchResourceHandler.companySearch()).thenReturn(privateCompanySearchHandler);
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldAddUkEstablishmentsForOverseaCompany() throws Exception {
        // Given
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType("oversea-company");
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER);

        when(privateCompanyResourceHandler.getCompanyFullProfile(anyString()))
                .thenReturn(privateCompanyFullProfileGet);
        when(privateCompanyFullProfileGet.execute()).thenReturn(apiResponse);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(overseaCompany));
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(ukEstablishments);

        // When
        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        // Then
        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, apiResponse.getData());
        verify(privateCompanySearchHandler).upsertCompanyProfile(UK_ESTABLISHMENT_URI, apiResponse.getData());
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldNotAddUkEstablishmentsForNonOverseaCompany() throws Exception {
        // Given
        CompanyProfile nonOverseaCompany = new CompanyProfile();
        nonOverseaCompany.setType("ltd");

        when(privateCompanyResourceHandler.getCompanyFullProfile(anyString()))
                .thenReturn(privateCompanyFullProfileGet);
        when(privateCompanyFullProfileGet.execute()).thenReturn(apiResponse);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(nonOverseaCompany));

        // When
        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        // Then
        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, apiResponse.getData());
        verify(companyProfileService, never()).findUkEstablishmentsByParent(anyString());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldDeleteUkEstablishmentsForOverseaCompany() throws Exception {
        // Given
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType("oversea-company");
        List<String> ukEstablishments = List.of(UK_ESTABLISHMENT_NUMBER);

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(overseaCompany));
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(ukEstablishments);
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        // When
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        // Then
        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
        verify(privateCompanySearchHandler).deleteCompanyProfile(UK_ESTABLISHMENT_URI);
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldNotDeleteUkEstablishmentsForNonOverseaCompany() throws Exception {
        // Given
        CompanyProfile nonOverseaCompany = new CompanyProfile();
        nonOverseaCompany.setType("ltd");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(nonOverseaCompany));
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        // When
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        // Then
        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
        verify(companyProfileService, never()).findUkEstablishmentsByParent(anyString());
    }

    @Test
    void addCompanyIntoElasticSearchIndex_ShouldHandleEmptyEstablishmentsList() throws Exception {
        // Given
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType("oversea-company");

        when(privateCompanyResourceHandler.getCompanyFullProfile(anyString()))
                .thenReturn(privateCompanyFullProfileGet);
        when(privateCompanyFullProfileGet.execute()).thenReturn(apiResponse);
        when(privateCompanySearchHandler.upsertCompanyProfile(anyString(), any()))
                .thenReturn(privateCompanySearchUpsert);
        when(privateCompanySearchUpsert.execute()).thenReturn(SUCCESS_RESPONSE);
        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(overseaCompany));
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());

        // When
        CompanyData companyData = new CompanyData(COMPANY_NUMBER, "authCode", "companyUri");
        service.addCompanyIntoElasticSearchIndex(companyData);

        // Then
        verify(privateCompanySearchHandler).upsertCompanyProfile(URI, apiResponse.getData());
        verify(privateCompanySearchHandler, times(1)).upsertCompanyProfile(anyString(), any());
    }

    @Test
    void deleteCompanyFromElasticSearchIndex_ShouldHandleEmptyEstablishmentsList() throws Exception {
        // Given
        CompanyProfile overseaCompany = new CompanyProfile();
        overseaCompany.setType("oversea-company");

        when(companyProfileService.getCompanyProfile(COMPANY_NUMBER))
                .thenReturn(Optional.of(overseaCompany));
        when(companyProfileService.findUkEstablishmentsByParent(COMPANY_NUMBER))
                .thenReturn(Collections.emptyList());
        when(privateCompanySearchHandler.deleteCompanyProfile(anyString()))
                .thenReturn(privateCompanySearchDelete);
        when(privateCompanySearchDelete.execute()).thenReturn(SUCCESS_RESPONSE);

        // When
        service.deleteCompanyFromElasticSearchIndex(COMPANY_NUMBER);

        // Then
        verify(privateCompanySearchHandler).deleteCompanyProfile(URI);
        verify(privateCompanySearchHandler, times(1)).deleteCompanyProfile(anyString());
    }
}