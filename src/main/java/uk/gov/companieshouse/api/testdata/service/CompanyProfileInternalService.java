package uk.gov.companieshouse.api.testdata.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import uk.gov.companieshouse.api.company.CompanyProfile;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface CompanyProfileInternalService {
    void createCompanyProfileData(CompanySpec companySpec, String deltaAt) throws DataException, JsonProcessingException;
    void deleteCompanyProfileData(String deltaAt, String companyNumber) throws DataException;
}
