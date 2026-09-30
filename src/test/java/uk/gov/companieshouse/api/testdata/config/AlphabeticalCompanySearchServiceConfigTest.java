package uk.gov.companieshouse.api.testdata.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.testdata.service.impl.AlphabeticalCompanySearchImpl;

class AlphabeticalCompanySearchServiceConfigTest {

    private AlphabeticalCompanySearchServiceConfig config;
    private Supplier<InternalApiClient> internalApiClientSupplier;

    @BeforeEach
    void setUp() {
        config = new AlphabeticalCompanySearchServiceConfig();
        internalApiClientSupplier = mock(Supplier.class);
    }

    @Test
    void alphabeticalCompanySearchService_ShouldCreateBeanWithEmptyInstance() {
        AlphabeticalCompanySearchImpl service = config.alphabeticalCompanySearchService(internalApiClientSupplier);

        assertNotNull(service);
    }

    @Test
    void greenAlphabeticalCompanySearchService_ShouldCreateBeanWithGreenInstance() {
        AlphabeticalCompanySearchImpl service = config.greenAlphabeticalCompanySearchService(internalApiClientSupplier);

        assertNotNull(service);
    }

    @Test
    void alphabeticalCompanySearchService_ShouldHaveDifferentInstanceThanGreenService() {
        AlphabeticalCompanySearchImpl service = config.alphabeticalCompanySearchService(internalApiClientSupplier);
        AlphabeticalCompanySearchImpl greenService = config.greenAlphabeticalCompanySearchService(internalApiClientSupplier);

        assertNotNull(service);
        assertNotNull(greenService);
    }
}
