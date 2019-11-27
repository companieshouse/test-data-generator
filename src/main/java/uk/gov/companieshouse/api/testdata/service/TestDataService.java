package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface TestDataService {
    /**
     * Create company data with given {@code companySpec}
     * 
     * @param companySpec The specification the new company must adhere to
     * @return A {@link CompanyData}
     * @throws DataException If any exception occurs
     */
    CompanyData createCompanyData(CompanySpec companySpec) throws DataException;

    /**
     * Delete all data for company {@code companyNumber}
     * 
     * @param companyNumber The company number to be deleted
     * @throws NoDataFoundException
     * @throws DataException
     */
    void deleteCompanyData(String companyNumber) throws NoDataFoundException, DataException;
}
