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
        String searchUri(String path, String queryParam, String queryValue) {
            return buildSearchUri(path, queryParam, queryValue);
        }
    }

    @Mock
    private RestTemplate restTemplate;

    private TestCompanySearch service;

    @BeforeEach
    void setUp() {
        service = new TestCompanySearch();
        service.restTemplate = restTemplate;
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
    void companyExistsReturnsTrueWhenTopHitPresent() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company(COMPANY_NUMBER));
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsTrueWhenOnlyItemsPresent() {
        // The /search/companies response carries no top_hit, only an items array
        var body = new LinkedHashMap<String, Object>();
        body.put("items", List.of(company("99999999"), company(COMPANY_NUMBER)));
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenTopHitIsEmpty() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", new LinkedHashMap<String, Object>());
        stubResponse(body, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenItemsIsEmpty() {
        var body = new LinkedHashMap<String, Object>();
        body.put("items", List.of());
        stubResponse(body, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenResultFieldsAreNotTheExpectedShape() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", "not-a-map");
        body.put("items", "not-a-list");
        stubResponse(body, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenResultsAreEmpty() {
        stubResponse(new LinkedHashMap<String, Object>(), HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenBodyIsNull() {
        stubResponse(null, HttpStatus.OK);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenStatusIsNotOk() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company(COMPANY_NUMBER));
        stubResponse(body, HttpStatus.NO_CONTENT);

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchUriIsNullOrBlank() {
        assertFalse(service.companyExists(null));
        assertFalse(service.companyExists("   "));

        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchReturnsNotFound() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.NOT_FOUND,
                        "Not Found", null, null, null));

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseOnOtherClientErrors() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(HttpClientErrorException.create(HttpStatus.UNAUTHORIZED,
                        "Unauthorized", null, null, null));

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsReturnsFalseWhenSearchApiIsUnreachable() {
        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(), eq(Map.class)))
                .thenThrow(new ResourceAccessException("connection refused"));

        assertFalse(service.companyExists(SEARCH_URI));
    }

    @Test
    void companyExistsCallsSearchApiWithApiKeyHeaderAndAbsoluteUrl() {
        stubResponse(new LinkedHashMap<String, Object>(), HttpStatus.OK);

        service.companyExists(SEARCH_URI);

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<HttpEntity> entity = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), entity.capture(),
                eq(Map.class));

        assertEquals(API_URL + SEARCH_URI, url.getValue());
        assertEquals(API_KEY, entity.getValue().getHeaders().getFirst("Authorization"));
    }

    @Test
    void companyExistsByQueryBuildsTheSearchUriAndReportsAHit() {
        var body = new LinkedHashMap<String, Object>();
        body.put("top_hit", company(COMPANY_NUMBER));
        stubResponse(body, HttpStatus.OK);

        assertTrue(service.companyExists("/advanced-search/companies",
                "company_name_includes", "SMITH & CO LTD"));

        ArgumentCaptor<String> url = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).exchange(url.capture(), eq(HttpMethod.GET), any(), eq(Map.class));
        assertEquals(API_URL + "/advanced-search/companies"
                + "?company_name_includes=SMITH+%26+CO+LTD", url.getValue());
    }

    @Test
    void companyExistsByQueryAssumesIndexedWhenQueryValueIsNullOrBlank() {
        // Without a value the index cannot be queried, so the caller must still attempt the delete
        assertTrue(service.companyExists("/advanced-search/companies",
                "company_name_includes", null));
        assertTrue(service.companyExists("/advanced-search/companies",
                "company_name_includes", "   "));

        verify(restTemplate, never()).exchange(anyString(), eq(HttpMethod.GET), any(),
                eq(Map.class));
    }

    @Test
    void buildSearchUriEncodesQueryValue() {
        assertEquals("/advanced-search/companies?company_name_includes=SMITH+%26+CO+LTD",
                service.searchUri("/advanced-search/companies", "company_name_includes",
                        "SMITH & CO LTD"));
    }
}
