package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.OfficerAppointmentService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@Service
public class TestDataServiceImpl implements TestDataService {

    private static final int COMPANY_NUMBER_LENGTH = 8;

    @Autowired
    private DataService<CompanyProfile> companyProfileService;
    @Autowired
    private DataService<FilingHistory> filingHistoryService;
    @Autowired
    private OfficerAppointmentService officerAppointmentService;
    @Autowired
    private DataService<CompanyAuthCode> companyAuthCodeService;
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
            new IllegalArgumentException("CompanySpec can not be null");
        }
        String companyNumber = String.valueOf(randomService.getNumber(COMPANY_NUMBER_LENGTH));
        spec.setCompanyNumber(companyNumber);
        
        this.companyProfileService.create(spec);
        this.filingHistoryService.create(spec);
        Appointment appointment = this.appointmentService.create(spec);
        this.officerAppointmentService.create(spec, appointment.getOfficerId(), appointment.getAppointmentId());
        CompanyAuthCode authCode = this.companyAuthCodeService.create(spec);
        this.companyMetricsService.create(spec);
        this.companyPscStatementService.create(spec);

        return new CompanyData(companyNumber, authCode.getAuthCode());
    }

    @Override
    public void deleteCompanyData(String companyId) throws NoDataFoundException, DataException {

        this.companyProfileService.delete(companyId);
        this.filingHistoryService.delete(companyId);
        this.officerAppointmentService.delete(companyId);
        this.companyAuthCodeService.delete(companyId);
        this.appointmentService.delete(companyId);
        this.companyPscStatementService.delete(companyId);
    }
}
