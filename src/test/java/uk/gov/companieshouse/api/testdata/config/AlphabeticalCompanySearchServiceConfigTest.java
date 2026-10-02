package uk.gov.companieshouse.api.testdata.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.testdata.service.impl.AlphabeticalCompanySearchImpl;

class AlphabeticalCompanySearchServiceConfigTest {

    private AlphabeticalCompanySearchServiceConfig config;
    private Supplier<InternalApiClient> internalApiClientSupplier;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        config = new AlphabeticalCompanySearchServiceConfig();
        internalApiClientSupplier = mock(Supplier.class);
        restTemplate = mock(RestTemplate.class);
    }

    @Test
    void alphabeticalCompanySearchService_ShouldCreateBeanWithEmptyInstance() {
        AlphabeticalCompanySearchImpl service = config.alphabeticalCompanySearchService(internalApiClientSupplier, restTemplate);

        assertNotNull(service);
    }

    @Test
    void greenAlphabeticalCompanySearchService_ShouldCreateBeanWithGreenInstance() {
        AlphabeticalCompanySearchImpl service = config.greenAlphabeticalCompanySearchService(internalApiClientSupplier, restTemplate);

        assertNotNull(service);
    }

    @Test
    void alphabeticalCompanySearchService_ShouldHaveDifferentInstanceThanGreenService() {
        AlphabeticalCompanySearchImpl service = config.alphabeticalCompanySearchService(internalApiClientSupplier, restTemplate);
        AlphabeticalCompanySearchImpl greenService = config.greenAlphabeticalCompanySearchService(internalApiClientSupplier, restTemplate);

        assertNotNull(service);
        assertNotNull(greenService);
    }
}

