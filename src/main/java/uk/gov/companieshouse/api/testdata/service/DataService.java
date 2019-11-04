package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface DataService<T> {
    T create(String companyNumber) throws DataException;

    void delete(String companyNumber) throws NoDataFoundException, DataException;
}
