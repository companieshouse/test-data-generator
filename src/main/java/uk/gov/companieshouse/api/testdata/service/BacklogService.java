package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface BacklogService {
    void updateBacklogCaseId(String backlogId, String caseId)
            throws NoDataFoundException, DataException;
}