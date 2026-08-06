package uk.gov.companieshouse.api.testdata.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.PscDiscrepancies;
import uk.gov.companieshouse.api.testdata.model.entity.PscDiscrepancyReports;
import uk.gov.companieshouse.api.testdata.model.rest.request.PscDiscrepanciesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PscDiscrepanciesResponse;
import uk.gov.companieshouse.api.testdata.repository.PscDiscrepanciesRepository;
import uk.gov.companieshouse.api.testdata.repository.PscDiscrepancyReportsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@ExtendWith(MockitoExtension.class)
class PscDiscrepanciesServiceImplTest {

    private static final String PSC_DISCREPANCY_ID =
            "5d03f438-972b-4d60-b0ae-f32ae8e22833";

    private static final String PSC_DISCREPANCY_REPORT_ID =
            "c119c044-2c78-4083-b633-c397e4f64eda";

    @Mock
    private PscDiscrepanciesRepository pscDiscrepanciesRepository;

    @Mock
    private PscDiscrepancyReportsRepository pscDiscrepancyReportsRepository;

    @Mock
    private RandomService randomService;

    @InjectMocks
    private PscDiscrepanciesServiceImpl service;

    @Test
    void createPscDiscrepancySuccess() throws Exception {

        PscDiscrepanciesRequest request =
                new PscDiscrepanciesRequest();
        request.setUserId("user-id");
        request.setPscType("individual-person-with-significant-control");

        when(randomService.getEtag())
                .thenReturn("test-etag");

        PscDiscrepanciesResponse response =
                service.create(request);

        assertNotNull(response);
        assertNotNull(response.getPscDiscrepanciesId());
        assertNotNull(response.getPscDiscrepancyReportId());

        verify(pscDiscrepancyReportsRepository, times(1))
                .save(any(PscDiscrepancyReports.class));

        verify(pscDiscrepanciesRepository, times(1))
                .save(any(PscDiscrepancies.class));
    }

    @Test
    void createPscDiscrepancyCreatesExpectedReport() throws Exception {

        PscDiscrepanciesRequest request =
                new PscDiscrepanciesRequest();
        request.setUserId("user-id");
        request.setPscType("corporate-entity");

        when(randomService.getEtag())
                .thenReturn("test-etag");

        service.create(request);

        ArgumentCaptor<PscDiscrepancyReports> reportCaptor =
                ArgumentCaptor.forClass(PscDiscrepancyReports.class);

        verify(pscDiscrepancyReportsRepository)
                .save(reportCaptor.capture());

        PscDiscrepancyReports report = reportCaptor.getValue();

        assertNotNull(report.getId());
        assertTrue(report.getLinks().getSelf()
                .contains("/psc-discrepancy-reports/"));
        assertTrue(report.getStatus().equals("INCOMPLETE"));
        assertTrue(report.getPscType().equals("corporate-entity"));
    }

    @Test
    void createPscDiscrepancyCreatesExpectedDiscrepancy() throws Exception {

        PscDiscrepanciesRequest request =
                new PscDiscrepanciesRequest();
        request.setUserId("user-id");
        request.setPscType("legal-person");

        when(randomService.getEtag())
                .thenReturn("test-etag");

        service.create(request);

        ArgumentCaptor<PscDiscrepancies> discrepancyCaptor =
                ArgumentCaptor.forClass(PscDiscrepancies.class);

        verify(pscDiscrepanciesRepository)
                .save(discrepancyCaptor.capture());

        PscDiscrepancies discrepancy =
                discrepancyCaptor.getValue();

        assertNotNull(discrepancy.getId());
        assertNotNull(discrepancy.getLinks());
        assertNotNull(discrepancy.getLinks().getSelf());
        assertNotNull(
                discrepancy.getLinks().getPscDiscrepancyReport());

        assertTrue(
                "legal-person".equals(discrepancy.getPscType()));
    }

    @Test
    void deletePscDiscrepancySuccess() {

        Links links = new Links();
        links.setPscDiscrepancyReport(
                "/psc-discrepancy-reports/"
                        + PSC_DISCREPANCY_REPORT_ID);

        PscDiscrepancies discrepancy =
                new PscDiscrepancies();
        discrepancy.setLinks(links);

        PscDiscrepancyReports report =
                new PscDiscrepancyReports();

        when(pscDiscrepanciesRepository.findById(
                PSC_DISCREPANCY_ID))
                .thenReturn(Optional.of(discrepancy));

        when(pscDiscrepancyReportsRepository.findById(
                PSC_DISCREPANCY_REPORT_ID))
                .thenReturn(Optional.of(report));

        boolean result =
                service.delete(PSC_DISCREPANCY_ID);

        assertTrue(result);

        verify(pscDiscrepanciesRepository, times(1))
                .delete(discrepancy);

        verify(pscDiscrepancyReportsRepository, times(1))
                .delete(report);
    }

    @Test
    void deletePscDiscrepancyNotFound() {

        when(pscDiscrepanciesRepository.findById(
                PSC_DISCREPANCY_ID))
                .thenReturn(Optional.empty());

        boolean result =
                service.delete(PSC_DISCREPANCY_ID);

        assertFalse(result);

        verify(pscDiscrepanciesRepository, times(0))
                .delete(any());

        verify(pscDiscrepancyReportsRepository, times(0))
                .delete(any());
    }

    @Test
    void deletePscDiscrepancyWhenReportNotFound() {

        Links links = new Links();

        links.setPscDiscrepancyReport(
                "/psc-discrepancy-reports/"
                        + PSC_DISCREPANCY_REPORT_ID);

        PscDiscrepancies discrepancy =
                new PscDiscrepancies();
        discrepancy.setLinks(links);

        when(pscDiscrepanciesRepository.findById(
                PSC_DISCREPANCY_ID))
                .thenReturn(Optional.of(discrepancy));

        when(pscDiscrepancyReportsRepository.findById(
                PSC_DISCREPANCY_REPORT_ID))
                .thenReturn(Optional.empty());

        boolean result =
                service.delete(PSC_DISCREPANCY_ID);

        assertTrue(result);

        verify(pscDiscrepanciesRepository, times(1))
                .delete(discrepancy);

        verify(pscDiscrepancyReportsRepository, times(0))
                .delete(any());
    }

    @Test
    void deletePscDiscrepancyThrowsNullPointerWhenReportLinkMissing() {

        Links links = new Links();
        links.setPscDiscrepancyReport(null);

        PscDiscrepancies discrepancy =
                new PscDiscrepancies();
        discrepancy.setLinks(links);

        when(pscDiscrepanciesRepository.findById(
                PSC_DISCREPANCY_ID))
                .thenReturn(Optional.of(discrepancy));

        assertThrows(
                NullPointerException.class,
                () -> service.delete(PSC_DISCREPANCY_ID));
    }

    @Test
    void getCurrentDateTimeReturnsValue() {

        Instant result = service.getCurrentDateTime();

        assertNotNull(result);

        assertDoesNotThrow(
                service::getCurrentDateTime);
    }
}