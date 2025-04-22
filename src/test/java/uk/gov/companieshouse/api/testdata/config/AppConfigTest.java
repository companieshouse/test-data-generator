package uk.gov.companieshouse.api.testdata.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.time.Clock;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.http.ApiKeyHttpClient;

class AppConfigTest {

    private static final String API_KEY = "test-api-key";
    private static final String API_URL = "http://test-api-url";

    private AppConfig appConfig;

    @Mock
    private ApiKeyHttpClient apiKeyHttpClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        appConfig = new AppConfig(API_KEY, API_URL);
    }

    @Test
    void clock_ShouldReturnNonNullClock() {
        Clock clock = appConfig.clock();
        assertNotNull(clock);
    }

    @Test
    void internalApiClientSupplier_ShouldReturnInternalApiClient() {
        // Mock DataMapHolder behavior
        DataMapHolder.initialise("test-request-id");

        Supplier<InternalApiClient> supplier = appConfig.internalApiClientSupplier();
        InternalApiClient internalApiClient = supplier.get();

        assertNotNull(internalApiClient);
        assertEquals(API_URL, internalApiClient.getBasePath());

        verify(apiKeyHttpClient, never()).setRequestId(anyString()); // Ensure no direct interaction
    }
}
