package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;

public interface CompanySearchService {

    void addCompanyIntoElasticSearchIndex(CompanyProfileResponse data) throws DataException, ApiErrorResponseException, URIValidationException;

    void deleteCompanyFromElasticSearchIndex(String companyNumber) throws DataException, ApiErrorResponseException, URIValidationException;
}
