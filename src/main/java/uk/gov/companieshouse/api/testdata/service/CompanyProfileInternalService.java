package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpecInternal;

public interface CompanyProfileInternalService {
    CompanyData create(CompanySpecInternal companySpecInternal, String apiKey) throws DataException;

}
