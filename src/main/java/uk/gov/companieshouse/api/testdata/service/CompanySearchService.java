package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;

public interface CompanySearchService {

    void addCompanyIntoElasticSearchIndex(CompanyData data) throws DataException, ApiErrorResponseException, URIValidationException;
}
