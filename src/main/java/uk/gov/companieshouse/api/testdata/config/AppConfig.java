package uk.gov.companieshouse.api.testdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Main application configuration class.
 */
@Configuration
public class AppConfig {
    /**
     * Obtains a clock that returns the current instant, converting to date and time using the
     * UTC time-zone. Singleton bean provides consistent UTC timestamps.
     *
     * @return a Clock that uses the best available system clock in the UTC zone, not null
     */
    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }

}