package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.companyprofile.Company;

public interface ICompanyProfileService {
    Company create() throws DataException;

    void delete(String companyId) throws NoDataFoundException, DataException;
}
