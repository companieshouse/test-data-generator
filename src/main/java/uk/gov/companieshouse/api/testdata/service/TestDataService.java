package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;

public interface TestDataService {
    CompanyData createCompanyData() throws DataException;

    void deleteCompanyData(String companyId) throws NoDataFoundException, DataException;
}
