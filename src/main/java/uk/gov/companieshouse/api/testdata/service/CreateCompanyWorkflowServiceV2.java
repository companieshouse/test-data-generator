package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;

public interface CreateCompanyWorkflowServiceV2 {

    CompanyProfileResponse createPublicCompanyV2(PublicCompanyRequestV2 companySpec) throws DataException;
}

