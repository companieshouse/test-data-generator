package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface DataService<T> {
    T create(CompanySpec companySpec) throws DataException;

    void delete(String companyNumber) throws NoDataFoundException, DataException;
}
