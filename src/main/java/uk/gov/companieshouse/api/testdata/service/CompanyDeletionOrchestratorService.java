package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface CompanyDeletionOrchestratorService {

	void deleteCompany(String companyNumber) throws DataException, NoDataFoundException;
}

