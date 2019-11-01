package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.model.companyauthcode.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.officer.Officer;
import uk.gov.companieshouse.api.testdata.model.psc.PersonsWithSignificantControl;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;

@Service
public class TestDataServiceImpl implements TestDataService {

    private static final int COMPANY_NUMBER_LENGTH = 8;

    private DataService<Company> companyProfileService;
    private DataService<FilingHistory> filingHistoryService;
    private DataService<Officer> officerListService;
    private DataService<PersonsWithSignificantControl> pscService;
    private DataService<CompanyAuthCode> companyAuthCodeService;
    private RandomService randomService;

    @Autowired
    public TestDataServiceImpl(DataService<Company> companyProfileService, DataService<FilingHistory> filingHistoryService,
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
    public CreatedCompany createCompanyData() throws DataException {
        String companyNumber = randomService.getRandomInteger(COMPANY_NUMBER_LENGTH);

        this.companyProfileService.create(companyNumber);
        this.filingHistoryService.create(companyNumber);
        this.officerListService.create(companyNumber);
        this.pscService.create(companyNumber);
        CompanyAuthCode authCode = this.companyAuthCodeService.create(companyNumber);

        return new CreatedCompany(companyNumber, authCode.getAuthCode());
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
