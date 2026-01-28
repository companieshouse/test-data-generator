package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CombinedCompanySpec;

public interface CombinedTdgCompanyService {
    void createCombinedCompany(CombinedCompanySpec companySpec);
}
