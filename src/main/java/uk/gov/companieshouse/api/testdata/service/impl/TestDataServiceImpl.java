package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
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
    private DataService<CompanyProfile> companyProfileService;
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
    private RandomService randomService;

    @Override
    public CompanyData createCompanyData(final CompanySpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanySpec can not be null");
        }
        String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix();
        String companyNumber = companyNumberPrefix +
                randomService.getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length());

        spec.setCompanyNumber(companyNumber);

        try {
            this.companyProfileService.create(spec);
            this.filingHistoryService.create(spec);
            this.appointmentService.create(spec);
            CompanyAuthCode authCode = this.companyAuthCodeService.create(spec);
            this.companyMetricsService.create(spec);
            this.companyPscStatementService.create(spec);

            return new CompanyData(companyNumber, authCode.getAuthCode());
        } catch (DataException ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", companyNumber);
            LOG.error("Rolling back creation of company", data);
            // Rollback all successful insertions
            deleteCompanyData(spec.getCompanyNumber());
            throw ex;
        }
    }

    @Override
    public void deleteCompanyData(String companyId) throws DataException {
        Optional<DataException> throwEx = Optional.empty();
        try {
            this.companyProfileService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }
        try {
            this.filingHistoryService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }
        try {
            this.companyAuthCodeService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }
        try {
            this.appointmentService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }
        try {
            this.companyPscStatementService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }
        try {
            this.companyMetricsService.delete(companyId);
        } catch (DataException de) {
            throwEx = addOrSuppressException(throwEx, de);
        }

        if (throwEx.isPresent()) {
            throw throwEx.get();
        }

    }
    
    private Optional<DataException> addOrSuppressException(Optional<DataException> throwEx, DataException suppressed) {
        if (throwEx.isPresent()) {
            throwEx.get().addSuppressed(suppressed);
        }
        return Optional.of(throwEx.orElse(suppressed));
    }
}
