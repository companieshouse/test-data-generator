package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteInternalCompanyRequest;

public interface DeleteCompanyWorkflowService {
    void deleteCompany(String companyNumber) throws DataException, NoDataFoundException;

    void deleteCompany(DeleteInternalCompanyRequest deleteInternalCompanyRequest, String companyNumber)
            throws DataException, NoDataFoundException;
    void deleteCompanyOptional(String companyNumber) throws DataException, NoDataFoundException;
}


