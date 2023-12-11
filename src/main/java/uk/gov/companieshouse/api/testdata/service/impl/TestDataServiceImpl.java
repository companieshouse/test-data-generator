package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.*;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class TestDataServiceImpl implements TestDataService {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private static final int COMPANY_NUMBER_LENGTH = 8;

    @Autowired
    private CompanyProfileService companyProfileService;
    @Autowired
    private DataService<FilingHistory> filingHistoryService;
    @Autowired
    private CompanyAuthCodeService companyAuthCodeService;
    @Autowired
    private DataService<Appointment> appointmentService;
    @Autowired
    private DataService<CompanyMetrics> companyMetricsService;
    @Autowired
    private DataService<CompanyPscStatement> companyPscStatementService;
    @Autowired
    private DataService<CompanyPscs> companyPscsService;
    @Autowired
    private RandomService randomService;

    @Value("${api.url}")
    private String apiUrl;

    void setAPIUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public CompanyData createCompanyData(final CompanySpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanySpec can not be null");
        }
        final String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix();

        do {
            if (!spec.isRegisteredEmailAddressChange()) {
                // company number format: PP+123456 (Prefix either 0 or 2 chars, example uses 2 chars)
                spec.setCompanyNumber(companyNumberPrefix
                        + randomService.getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
            } else {
                // company number format: PP+12345+ERR (Prefix either 0 or 2 chars, example uses 2 chars)
                spec.setCompanyNumber(companyNumberPrefix
                        + randomService.getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length() - 3) + "ERR");
            }
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));

        try {
            this.companyProfileService.create(spec);
            this.filingHistoryService.create(spec);
            this.appointmentService.create(spec);
            CompanyAuthCode authCode = this.companyAuthCodeService.create(spec);
            this.companyMetricsService.create(spec);
            this.companyPscStatementService.create(spec);
            this.companyPscsService.create(spec);
            this.companyPscsService.create(spec);
            this.companyPscsService.create(spec);

            String companyUri = this.apiUrl + "/company/" + spec.getCompanyNumber();
            return new CompanyData(spec.getCompanyNumber(), authCode.getAuthCode(), companyUri);
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", spec.getCompanyNumber());
            LOG.error("Rolling back creation of company", data);
            // Rollback all successful insertions
            deleteCompanyData(spec.getCompanyNumber());
            throw new DataException(ex);
        }
    }

    @Override
    public void deleteCompanyData(String companyId) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();

        try {
            this.companyProfileService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.filingHistoryService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyAuthCodeService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.appointmentService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscStatementService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscsService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyMetricsService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }


        if (!suppressedExceptions.isEmpty()) {
            DataException ex = new DataException("Error deleting company data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }
    }
    
}
