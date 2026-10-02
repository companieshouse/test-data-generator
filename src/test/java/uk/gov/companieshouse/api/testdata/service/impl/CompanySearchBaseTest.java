package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class CompanySearchBaseTest {

    private static final String API_URL = "http://api.chs.local";
    private static final String API_KEY = "test-api-key";
    private static final String COMPANY_NUMBER = "12345678";
    private static final String SEARCH_URI = "/search/companies?q=test";

    /** Minimal concrete subclass so the abstract base can be exercised directly. */
    private static class TestCompanySearch extends CompanySearchBase {
        TestCompanySearch(RestTemplate restTemplate) {
            super(restTemplate);
        }

        String searchUri(String path, String queryParam, String queryValue) {
            return buildSearchUri(path, queryParam, queryValue);
        }
    }

    @Mock
    private RestTemplate restTemplate;

    private TestCompanySearch service;

    @BeforeEach
    void setUp() {
        service = new TestCompanySearch(restTemplate);
        service.apiUrl = API_URL;
        service.apiKey = API_KEY;
    }

    private void stubResponse(Object body, HttpStatus status) {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenReturn(new ResponseEntity(body, status));
    }

    private static Map<String, Object> company(String companyNumber) {
        var company = new LinkedHashMap<String, Object>();
        company.put("company_name", "COMPANY " + companyNumber + " LTD");
        company.put("company_number", companyNumber);
        return company;
    }

    @Test
    void companyExistsReturnsTrueWhenTopHitMatches() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company(COMPANY_NUMBER));
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsTrueWhenOnlyItemsPresent() {
        // The /search/companies response carries no top_hit, only an items array
        var body = new LinkedHashMap<String, Object>();
        body.put("items", List.of(company("99999999"), company(COMPANY_NUMBER)));
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsIsCaseInsensitiveAndIgnoresSurroundingWhitespace() {
        var hit = new LinkedHashMap<String, Object>();
        hit.put("company_number", "  sc123456  ");
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", hit);
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists(SEARCH_URI, "SC123456"));
    }

    @Test
    void companyExistsReturnsFalseWhenADifferentCompanyMatches() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company("99999999"));
        body.put("items", List.of(company("88888888")));
        stubResponse(body, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenCompanyNumberFieldMissing() {
        var hit = new LinkedHashMap<String, Object>();
        hit.put("company_name", "COMPANY 12345678 LTD");
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", hit);
        stubResponse(body, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenResultsAreEmpty() {
        stubResponse(new LinkedHashMap<String, Object>(), HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenBodyIsNull() {
        stubResponse(null, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenStatusIsNotOk() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company(COMPANY_NUMBER));
        stubResponse(body, HttpStatus.NO_CONTENT);

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchUriIsNullOrBlank() {
        assertFalse(service.companyExists(null, COMPANY_NUMBER));
        assertFalse(service.companyExists("   ", COMPANY_NUMBER));

        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void companyExistsReturnsFalseWhenCompanyNumberIsNullOrBlank() {
        assertFalse(service.companyExists(SEARCH_URI, null));
        assertFalse(service.companyExists(SEARCH_URI, "   "));

        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchReturnsNotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND,
                        "Not Found", null, null, null));

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseOnOtherClientErrors() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.UNAUTHORIZED,
                        "Unauthorized", null, null, null));

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchApiIsUnreachable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertFalse(service.companyExists(SEARCH_URI, COMPANY_NUMBER));
    }

    @Test
    void companyExistsCallsSearchApiWithApiKeyHeaderAndAbsoluteUrl() {
        stubResponse(new LinkedHashMap<String, Object>(), HttpStatus.OK);

        service.companyExists(SEARCH_URI, COMPANY_NUMBER);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), entity.capture(),
                eq(Map.class));

        assertEquals(API_URL + SEARCH_URI, url.getValue());
        assertEquals(API_KEY, entity.getValue().getHeaders().getFirst("Authorization"));
    }

    @Test
    void buildSearchUriEncodesQueryValue() {
        assertEquals("/advanced-search/companies?company_name_includes=SMITH+%26+CO+LTD",
                service.searchUri("/advanced-search/companies", "company_name_includes",
                        "SMITH & CO LTD"));
    }
}
