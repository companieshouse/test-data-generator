package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CombinedCompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;

public interface CombinedTdgCompanyService {
    void createCombinedCompany(CombinedCompanySpec companySpec);
}
