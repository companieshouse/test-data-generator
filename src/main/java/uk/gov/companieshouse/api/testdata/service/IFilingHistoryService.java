package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.filinghistory.FilingHistory;

public interface IFilingHistoryService {
    FilingHistory create(String companyNumber) throws DataException;

    void delete(String companyId) throws NoDataFoundException, DataException;
}
