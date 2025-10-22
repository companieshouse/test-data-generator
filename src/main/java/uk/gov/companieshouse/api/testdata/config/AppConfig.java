package uk.gov.companieshouse.api.testdata.config;

import java.time.Clock;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;

/**
 * Main application configuration class.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {
    /**
     * Obtains a clock that returns the current instant, converting to date and time using the
     * UTC time-zone. Singleton bean provides consistent UTC timestamps.
     *
     * @return a Clock that uses the best available system clock in the UTC zone, not null
     */
    private final String chsApiKey;
    private final String apiUrl;

    public AppConfig(@Value("${api-key}") String chsApiKey, @Value("${api-url}") String apiUrl) {
        this.chsApiKey = chsApiKey;
        this.apiUrl = apiUrl;
    }

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    public Supplier<InternalApiClient> internalApiClientSupplier() {
        return () -> {
            var apiKeyHttpClient = new ApiKeyHttpClient(chsApiKey);
            apiKeyHttpClient.setRequestId(DataMapHolder.getRequestId());

            var internalApiClient = new InternalApiClient(apiKeyHttpClient);
            internalApiClient.setBasePath(apiUrl);
            internalApiClient.setInternalBasePath(apiUrl);

            return internalApiClient;
        };
    }

}