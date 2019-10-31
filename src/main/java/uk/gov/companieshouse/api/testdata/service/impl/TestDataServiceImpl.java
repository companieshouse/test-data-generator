package uk.gov.companieshouse.api.testdata.service.impl;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.CreatedCompany;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.service.ICompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.ICompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.IFilingHistoryService;
import uk.gov.companieshouse.api.testdata.service.IOfficerListService;
import uk.gov.companieshouse.api.testdata.service.IPSCService;
import uk.gov.companieshouse.api.testdata.service.ITestDataService;

@Service
public class TestDataServiceImpl implements ITestDataService {

    private ICompanyProfileService companyProfileService;
    private IFilingHistoryService filingHistoryService;
    private IOfficerListService officerListService;
    private IPSCService pscService;
    private ICompanyAuthCodeService companyAuthCodeService;

    @Autowired
    public TestDataServiceImpl(ICompanyProfileService companyProfileService, IFilingHistoryService filingHistoryService,
                               IOfficerListService officerListService, IPSCService pscService,
                               ICompanyAuthCodeService companyAuthCodeService) {

        this.companyProfileService = companyProfileService;
        this.filingHistoryService = filingHistoryService;
        this.officerListService = officerListService;
        this.pscService = pscService;
        this.companyAuthCodeService = companyAuthCodeService;
    }

    @Override
    public CreatedCompany createCompanyData() throws DataException {

        Company generatedCompany = this.companyProfileService.create();
        String companyNumber = generatedCompany.getCompanyNumber();
        this.filingHistoryService.create(companyNumber);
        this.officerListService.create(companyNumber);
        this.pscService.create(companyNumber);
        String authCode = this.companyAuthCodeService.create(companyNumber);

        return new CreatedCompany(companyNumber, authCode);
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
