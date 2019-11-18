package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Officer;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.DataService;
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
    private DataService<Officer> officerListService;
    @Autowired
    private DataService<CompanyAuthCode> companyAuthCodeService;
    @Autowired
    private DataService<Appointment> appointmentService;
    @Autowired
    private DataService<CompanyMetrics> companyMetricsService;
    @Autowired
    private RandomService randomService;

    @Override
    public CompanyData createCompanyData() throws DataException {
        String companyNumber = String.valueOf(randomService.getNumber(COMPANY_NUMBER_LENGTH));

        this.companyProfileService.create(companyNumber);
        this.filingHistoryService.create(companyNumber);
        this.officerListService.create(companyNumber);
        this.appointmentService.create(companyNumber);
        CompanyAuthCode authCode = this.companyAuthCodeService.create(companyNumber);
        this.companyMetricsService.create(companyNumber);

        return new CompanyData(companyNumber, authCode.getAuthCode());
    }

    @Override
    public void deleteCompanyData(String companyId) throws NoDataFoundException, DataException {

        this.companyProfileService.delete(companyId);
        this.filingHistoryService.delete(companyId);
        this.officerListService.delete(companyId);
        this.companyAuthCodeService.delete(companyId);
        this.appointmentService.delete(companyId);
    }
}
