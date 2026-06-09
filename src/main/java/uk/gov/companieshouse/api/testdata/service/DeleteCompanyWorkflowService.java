package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface DeleteCompanyWorkflowService {

	void deleteCompany(String companyNumber) throws DataException, NoDataFoundException;
}


