package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.company.Data;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpecInternal;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileInternalService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;
import uk.gov.companieshouse.api.company.CompanyProfile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Service
public class CompanyProfileInternalServiceImpl implements CompanyProfileInternalService {
    private static final String COMPANY_PROFILE_URI = "/company/%s/internal";

    @Value("${api.url}")
    private String apiUrl;

    @Value(("${api-key}"))
    private String apiKey;

    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(CompanySearchServiceImpl.class));


    @Override
    public CompanyData create(CompanySpecInternal spec, String apiKey1) throws DataException {
        var companyNumber = spec.getData().getCompanyNumber();
        String url = String.format("%s%s", apiUrl, String.format(COMPANY_PROFILE_URI, companyNumber));

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = apiKey + ":";
        byte[] encodedAuth = Base64.getEncoder().encode(auth.getBytes(StandardCharsets.UTF_8));
        String authHeader = "Basic " + new String(encodedAuth);
        headers.set("Authorization", authHeader); // Add if needed

        HttpEntity<CompanySpecInternal> requestEntity = new HttpEntity<>(spec, headers);

        try {
            ResponseEntity<CompanyData> response = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    CompanyData.class
            );
            return response.getBody();
        } catch (RestClientException e) {
            LOG.error("Error creating company profile", e);
            throw new DataException("Failed to create company profile", e);
        }
    }

}
