package com.poc.testdata.service;

import com.poc.testdata.exception.DataException;
import com.poc.testdata.exception.NoDataFoundException;
import com.poc.testdata.model.CompanyAuthCodes.CompanyAuthCode;
import com.poc.testdata.repository.CompanyAuthCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDataService {

    @Autowired
    CompanyProfileService companyProfileService;
    @Autowired
    FilingHistoryService filingHistoryService;
    @Autowired
    OfficerListService officerListService;
    @Autowired
    PSCService pscService;
    @Autowired
    CompanyAuthCodeService companyAuthCodeService;

    public void createCompanyData() throws DataException {

        companyProfileService.create();
        String companyNumber = companyProfileService.getCompanyNumber();
        filingHistoryService.create(companyNumber);
        officerListService.create(companyNumber);
        pscService.create(companyNumber);
        companyAuthCodeService.create(companyNumber);

    }

    public String getCompanyId(){

        return companyProfileService.getCompanyNumber();
    }

    public String getCompanyAuthenticationCode(){

        return companyAuthCodeService.getAuthenticationCode();
    }

    public void deleteCompanyData(String companyId) throws NoDataFoundException, DataException {

        companyProfileService.delete(companyId);
        filingHistoryService.delete(companyId);
        officerListService.delete(companyId);
        pscService.delete(companyId);
        companyAuthCodeService.delete(companyId);
    }
}
