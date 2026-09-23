package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyExemptions;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyExemptionsTimestamp;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyExemptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyExemptionsResponse;
import uk.gov.companieshouse.api.testdata.repository.CompanyExemptionsRepository;

class CompanyExemptionsServiceImplTest {

    private static final String COMPANY_NUMBER = "AC123456";

    @Mock
    private CompanyExemptionsRepository repository;

    @InjectMocks
    private CompanyExemptionsServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createCompanyExemptionsWithRandomType() throws DataException {
        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        when(repository.save(any(CompanyExemptions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyExemptionsResponse response = service.createOrUpdate(request);

        assertEquals(COMPANY_NUMBER, response.getCompanyNumber());
        assertNotNull(response.getDeltaAt());
        assertNotNull(response.getData());
        assertNotNull(response.getData().get("exemptions"));
        assertEquals("exemptions#exemptions", response.getData().get("kind"));
        assertNotNull(response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
        verify(repository, times(1)).save(any(CompanyExemptions.class));
    }

    @Test
    void createCompanyExemptionsWithSpecificType() throws DataException {
        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);
        request.setExemptionType("psc_exempt_as_trading_on_uk_regulated_market");

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        when(repository.save(any(CompanyExemptions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyExemptionsResponse response = service.createOrUpdate(request);

        assertEquals(COMPANY_NUMBER, response.getCompanyNumber());
        @SuppressWarnings("unchecked")
        Map<String, Object> exemptions = (Map<String, Object>) response.getData().get("exemptions");
        assertTrue(exemptions.containsKey("psc_exempt_as_trading_on_uk_regulated_market"));
    }

    @Test
    void updateCompanyExemptionsPreservesCreatedAt() throws DataException {
        CompanyExemptions existing = new CompanyExemptions();
        existing.setId(COMPANY_NUMBER);
        CompanyExemptionsTimestamp created = new CompanyExemptionsTimestamp();
        created.setAt(Instant.parse("2026-08-18T00:00:00Z"));
        existing.setCreated(created);

        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.of(existing));
        when(repository.save(any(CompanyExemptions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyExemptionsResponse response = service.createOrUpdate(request);

        assertEquals(Instant.parse("2026-08-18T00:00:00Z"), response.getCreatedAt());
        assertNotNull(response.getUpdatedAt());
    }

    @Test
    void createCompanyExemptionsWithProvidedData() throws DataException {
        Map<String, Object> customData = Map.of(
                "etag", "custom-etag",
                "kind", "exemptions#exemptions",
                "links", Map.of("self", "/company/" + COMPANY_NUMBER + "/exemptions"),
                "exemptions", Map.of("psc_exempt_as_trading_on_regulated_market",
                        Map.of("exemption_type", "psc-exempt-as-trading-on-regulated-market",
                                "items", java.util.List.of(Map.of("exempt_from", "2023-01-01", "exempt_to", "2024-01-01"))))
        );

        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);
        request.setData(customData);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        when(repository.save(any(CompanyExemptions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyExemptionsResponse response = service.createOrUpdate(request);

        assertEquals(COMPANY_NUMBER, response.getCompanyNumber());
        assertEquals("custom-etag", response.getData().get("etag"));
        assertEquals(customData, response.getData());
        verify(repository, times(1)).save(any(CompanyExemptions.class));
    }

    @Test
    void createCompanyExemptionsWithEmptyDataFallsBackToAutoGeneration() throws DataException {
        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);
        request.setData(Map.of());

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        when(repository.save(any(CompanyExemptions.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompanyExemptionsResponse response = service.createOrUpdate(request);

        assertEquals(COMPANY_NUMBER, response.getCompanyNumber());
        assertEquals("exemptions#exemptions", response.getData().get("kind"));
        assertNotNull(response.getData().get("exemptions"));
    }

    @Test
    void createCompanyExemptionsThrowsDataException() {
        CompanyExemptionsRequest request = new CompanyExemptionsRequest();
        request.setCompanyNumber(COMPANY_NUMBER);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("db error")).when(repository).save(any(CompanyExemptions.class));

        assertThrows(DataException.class, () -> service.createOrUpdate(request));
    }

    @Test
    void getCompanyExemptions() throws NoDataFoundException {
        CompanyExemptions entity = new CompanyExemptions();
        entity.setId(COMPANY_NUMBER);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.of(entity));
        CompanyExemptionsResponse response = service.getByCompanyNumber(COMPANY_NUMBER);

        assertEquals(COMPANY_NUMBER, response.getCompanyNumber());
    }

    @Test
    void getCompanyExemptionsNotFound() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        assertThrows(NoDataFoundException.class, () -> service.getByCompanyNumber(COMPANY_NUMBER));
    }

    @Test
    void deleteCompanyExemptions() {
        CompanyExemptions entity = new CompanyExemptions();
        entity.setId(COMPANY_NUMBER);

        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.of(entity));
        boolean deleted = service.deleteByCompanyNumber(COMPANY_NUMBER);

        assertTrue(deleted);
        verify(repository, times(1)).delete(entity);
    }

    @Test
    void deleteCompanyExemptionsNotFound() {
        when(repository.findById(COMPANY_NUMBER)).thenReturn(Optional.empty());
        boolean deleted = service.deleteByCompanyNumber(COMPANY_NUMBER);

        assertFalse(deleted);
        verify(repository, never()).delete(any(CompanyExemptions.class));
    }
}