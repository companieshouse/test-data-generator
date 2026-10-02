package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.testdata.config.AlphabeticalCompanySearchServiceConfig;

/**
 * The search services take their RestTemplate, api.url and api-key from inherited injected
 * fields rather than their constructors, so this verifies Spring actually populates them -
 * a silent null would only surface as an NPE at delete time.
 */
class CompanySearchWiringTest {

    private static final String API_URL = "http://api.chs.local";
    private static final String API_KEY = "test-api-key";

    @Configuration
    static class TestBeans {
        static final RestTemplate REST_TEMPLATE = new RestTemplate();

        @Bean
        static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
            return new PropertySourcesPlaceholderConfigurer();
        }

        @Bean
        RestTemplate restTemplate() {
            return REST_TEMPLATE;
        }

        @SuppressWarnings("unchecked")
        @Bean
        Supplier<InternalApiClient> internalApiClientSupplier() {
            return mock(Supplier.class);
        }
    }

    private AnnotationConfigApplicationContext context() {
        var ctx = new AnnotationConfigApplicationContext();
        ctx.getEnvironment().getPropertySources().addFirst(new MapPropertySource(
                "test", java.util.Map.of("api.url", API_URL, "api-key", API_KEY)));
        ctx.register(TestBeans.class, AlphabeticalCompanySearchServiceConfig.class);
        ctx.refresh();
        return ctx;
    }

    private void assertWired(CompanySearchBase service) {
        assertNotNull(service.restTemplate, "RestTemplate was not injected");
        assertSame(TestBeans.REST_TEMPLATE, service.restTemplate);
        assertEquals(API_URL, service.apiUrl);
        assertEquals(API_KEY, service.apiKey);
    }

    @Test
    void beanCreatedSearchServicesHaveTheirInheritedFieldsInjected() {
        try (var ctx = context()) {
            assertWired(ctx.getBean("alphabeticalCompanySearchService",
                    AlphabeticalCompanySearchImpl.class));
            assertWired(ctx.getBean("greenAlphabeticalCompanySearchService",
                    AlphabeticalCompanySearchImpl.class));
        }
    }
}
