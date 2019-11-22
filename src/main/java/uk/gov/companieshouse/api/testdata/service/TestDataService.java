package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

public interface TestDataService {
    /**
     * Create company data in the given {@code jurisdiction}
     * 
     * @param jurisdiction The jurisdiction the company will be in
     * @return A {@link CompanyData}
     * @throws DataException If any exception occurs
     */
    CompanyData createCompanyData(Jurisdiction jurisdiction) throws DataException;

    void deleteCompanyData(String companyId) throws NoDataFoundException, DataException;
}
