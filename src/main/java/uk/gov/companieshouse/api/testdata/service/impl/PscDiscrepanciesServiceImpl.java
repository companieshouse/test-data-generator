package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.EncryptedDiscrepancyData;
import uk.gov.companieshouse.api.testdata.model.entity.Links;
import uk.gov.companieshouse.api.testdata.model.entity.PscDiscrepancies;
import uk.gov.companieshouse.api.testdata.model.entity.PscDiscrepancyReports;
import uk.gov.companieshouse.api.testdata.model.rest.request.PscDiscrepanciesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PscDiscrepanciesResponse;
import uk.gov.companieshouse.api.testdata.repository.PscDiscrepanciesRepository;
import uk.gov.companieshouse.api.testdata.repository.PscDiscrepancyReportsRepository;
import uk.gov.companieshouse.api.testdata.service.PscDiscrepanciesService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class PscDiscrepanciesServiceImpl implements PscDiscrepanciesService {

    private final PscDiscrepanciesRepository pscDiscrepanciesRepository;
    private final PscDiscrepancyReportsRepository pscDiscrepancyReportsRepository;
    private final RandomService randomService;

    public PscDiscrepanciesServiceImpl(
            PscDiscrepanciesRepository pscDiscrepanciesRepository,
            PscDiscrepancyReportsRepository pscDiscrepancyReportsRepository,
            RandomService randomService) {

        this.pscDiscrepanciesRepository = pscDiscrepanciesRepository;
        this.pscDiscrepancyReportsRepository = pscDiscrepancyReportsRepository;
        this.randomService = randomService;
    }

    @Override
    public PscDiscrepanciesResponse create(PscDiscrepanciesRequest request)
            throws DataException {

        Instant now = getCurrentDateTime();

        String reportId = UUID.randomUUID().toString();
        String discrepancyId = UUID.randomUUID().toString();

        var report = buildPscDiscrepancyReport(
                reportId,
                request,
                now);

        pscDiscrepancyReportsRepository.save(report);

        var discrepancy = buildPscDiscrepancy(
                discrepancyId,
                reportId,
                request,
                now);

        pscDiscrepanciesRepository.save(discrepancy);

        var response = new PscDiscrepanciesResponse();
        response.setPscDiscrepanciesId(discrepancyId);
        response.setPscDiscrepancyReportId(reportId);

        return response;
    }

    private PscDiscrepancies buildPscDiscrepancy(
            String discrepancyId,
            String reportId,
            PscDiscrepanciesRequest request,
            Instant now) {

        var links = new Links();
        links.setPscDiscrepancyReport(
                "/psc-discrepancy-reports/" + reportId);
        links.setSelf(
                "/psc-discrepancy-reports/"
                        + reportId
                        + "/discrepancies/"
                        + discrepancyId);

        var discrepancy = new PscDiscrepancies();

        discrepancy.setId(discrepancyId);
        discrepancy.setCreatedAt(now);
        discrepancy.setKind("psc_discrepancy#psc_discrepancy_report");
        discrepancy.setEtag(randomService.getEtag());
        discrepancy.setLinks(links);
        discrepancy.setPscType(request.getPscType());

        var encryptedDiscrepancyData = new EncryptedDiscrepancyData();
        encryptedDiscrepancyData.setCipherText("test-data");
        encryptedDiscrepancyData.setContextKind(
                "psc_discrepancy#psc_discrepancy_report");
        encryptedDiscrepancyData.setContextId(reportId);

        discrepancy.setEncryptedDiscrepancyData(
                encryptedDiscrepancyData);

        return discrepancy;
    }

    private PscDiscrepancyReports buildPscDiscrepancyReport(
            String reportId,
            PscDiscrepanciesRequest request,
            Instant now) {

        var links = new Links();
        links.setSelf("/psc-discrepancy-reports/" + reportId);

        var encryptedReportData = new EncryptedDiscrepancyData();
        encryptedReportData.setCipherText("test-data");
        encryptedReportData.setContextKind(
                "psc_discrepancy_report#psc_discrepancy_report");
        encryptedReportData.setContextId(reportId);

        var report = new PscDiscrepancyReports();

        report.setId(reportId);
        report.setUserId(request.getUserId());
        report.setCreatedAt(now);
        report.setKind("psc_discrepancy_report#psc_discrepancy_report");
        report.setEtag(randomService.getEtag());
        report.setStatus("INCOMPLETE");
        report.setLinks(links);
        report.setEncryptedDiscrepancyData(encryptedReportData);
        report.setPscType(request.getPscType());
        return report;
    }

    @Override
    public boolean delete(String pscDiscrepanciesId) {

        var discrepancyOptional =
                pscDiscrepanciesRepository.findById(pscDiscrepanciesId);

        if (discrepancyOptional.isEmpty()) {
            return false;
        }

        var discrepancy = discrepancyOptional.get();

        String reportLink =
                discrepancy.getLinks().getPscDiscrepancyReport();

        String reportId =
                reportLink.substring(reportLink.lastIndexOf('/') + 1);

        pscDiscrepanciesRepository.delete(discrepancy);

        pscDiscrepancyReportsRepository.findById(reportId)
                .ifPresent(pscDiscrepancyReportsRepository::delete);

        return true;
    }

    protected Instant getCurrentDateTime() {
        return Instant.now().atZone(ZoneOffset.UTC).toInstant();
    }
}