package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyAuthAllowListResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyAuthAllowListRequest;

public interface CompanyAuthAllowListService extends
        DataService<CompanyAuthAllowListResponse, CompanyAuthAllowListRequest> {
    String getAuthId(String emailAddress);
}
