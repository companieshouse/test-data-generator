package uk.gov.companieshouse.api.testdata.service.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

public abstract class CompanySearchBase {

    private static final String COMPANY_NUMBER_FIELD = "company_number";
    private static final String TOP_HIT_FIELD = "top_hit";
    private static final String ITEMS_FIELD = "items";

    @Value("${api.url}")
    protected String apiUrl;

    @Value("${api-key}")
    protected String apiKey;

    protected RestTemplate restTemplate;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanySearchBase.class));

    protected CompanySearchBase(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Builds a search URI, URL encoding the query value so that company names containing
     * spaces or reserved characters are sent correctly.
     */
    protected String buildSearchUri(String path, String queryParam, String queryValue) {
        return path + "?" + queryParam + "="
                + URLEncoder.encode(queryValue, StandardCharsets.UTF_8);
    }

    /**
     * Queries a search index and reports whether it holds a document for the given company
     * number. The caller supplies the full search URI because each index is queried
     * differently, while the company number is used to confirm the correct company matched.
     */
    public boolean companyExists(String searchUri, String companyNumber) {
        if (searchUri == null || searchUri.isBlank()) {
            LOG.error("Search uri cannot be null or empty");
            return false;
        }
        if (companyNumber == null || companyNumber.isBlank()) {
            LOG.error("Company number cannot be null or empty");
            return false;
        }

        try {
            HttpEntity<Object> entity = new HttpEntity<>(createHeaders(apiKey));
            ResponseEntity<Map> response = restTemplate.exchange(
                    apiUrl + searchUri,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
                return false;
            }
            return containsCompanyNumber(response.getBody(), companyNumber);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                LOG.info("Company not found for search uri: " + searchUri);
                return false;
            }
            LOG.error("Error checking if company exists, error: " + ex.getMessage());
            return false;
        } catch (Exception ex) {
            LOG.error("Error checking if company exists, error: " + ex.getMessage());
            return false;
        }
    }

    private boolean containsCompanyNumber(Map<?, ?> body, String companyNumber) {
        if (matchesCompanyNumber(body.get(TOP_HIT_FIELD), companyNumber)) {
            return true;
        }
        if (body.get(ITEMS_FIELD) instanceof Collection<?> items) {
            return items.stream().anyMatch(item -> matchesCompanyNumber(item, companyNumber));
        }
        return false;
    }

    private boolean matchesCompanyNumber(Object candidate, String companyNumber) {
        if (candidate instanceof Map<?, ?> company) {
            Object number = company.get(COMPANY_NUMBER_FIELD);
            return number != null && companyNumber.equalsIgnoreCase(number.toString().trim());
        }
        return false;
    }

    protected HttpHeaders createHeaders(String apiKey) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);
        headers.set("Content-Type", "application/json");
        return headers;
    }
}
