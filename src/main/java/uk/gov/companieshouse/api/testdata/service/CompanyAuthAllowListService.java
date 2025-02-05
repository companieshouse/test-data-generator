package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;

public interface CompanyAuthAllowListService extends
        DataService<CompanyAuthAllowListData, CompanyAuthAllowListSpec> {
    String getAuthId(String emailAddress);
}
