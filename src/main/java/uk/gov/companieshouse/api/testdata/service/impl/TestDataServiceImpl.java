package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Officer;
import uk.gov.companieshouse.api.testdata.model.entity.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@Service
public class TestDataServiceImpl implements TestDataService {

    private static final int COMPANY_NUMBER_LENGTH = 8;

    private DataService<CompanyProfile> companyProfileService;
    private DataService<FilingHistory> filingHistoryService;
    private DataService<Officer> officerListService;
    private DataService<PersonsWithSignificantControl> pscService;
    private DataService<CompanyAuthCode> companyAuthCodeService;
    private RandomService randomService;

    @Autowired
    public TestDataServiceImpl(DataService<CompanyProfile> companyProfileService, DataService<FilingHistory> filingHistoryService,
                               DataService<Officer> officerListService, DataService<PersonsWithSignificantControl> pscService,
                               DataService<CompanyAuthCode> companyAuthCodeService, RandomService randomService) {
        this.companyProfileService = companyProfileService;
        this.filingHistoryService = filingHistoryService;
        this.officerListService = officerListService;
        this.pscService = pscService;
        this.companyAuthCodeService = companyAuthCodeService;
        this.randomService = randomService;
    }

    @Override
    public CompanyData createCompanyData() throws DataException {
        String companyNumber = randomService.getRandomInteger(COMPANY_NUMBER_LENGTH);

        this.companyProfileService.create(companyNumber);
        this.filingHistoryService.create(companyNumber);
        this.officerListService.create(companyNumber);
        this.pscService.create(companyNumber);
        CompanyAuthCode authCode = this.companyAuthCodeService.create(companyNumber);

        return new CompanyData(companyNumber, authCode.getAuthCode());
    }

    @Override
    public void deleteCompanyData(String companyId) throws NoDataFoundException, DataException {

        this.companyProfileService.delete(companyId);
        this.filingHistoryService.delete(companyId);
        this.officerListService.delete(companyId);
        this.pscService.delete(companyId);
        this.companyAuthCodeService.delete(companyId);
    }
}
