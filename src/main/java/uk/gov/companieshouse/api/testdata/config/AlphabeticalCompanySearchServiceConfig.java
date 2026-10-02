package uk.gov.companieshouse.api.testdata.config;

import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.testdata.service.impl.AlphabeticalCompanySearchImpl; 

@Configuration
public class AlphabeticalCompanySearchServiceConfig {

    @Bean
    public AlphabeticalCompanySearchImpl alphabeticalCompanySearchService(
            Supplier<InternalApiClient> internalApiClientSupplier,
            RestTemplate restTemplate) {
        return new AlphabeticalCompanySearchImpl(internalApiClientSupplier, "", restTemplate);
    }

    @Bean
    public AlphabeticalCompanySearchImpl greenAlphabeticalCompanySearchService(
            Supplier<InternalApiClient> internalApiClientSupplier,
            RestTemplate restTemplate) {
        return new AlphabeticalCompanySearchImpl(internalApiClientSupplier, "/green", restTemplate);
    }
}