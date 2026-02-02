package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.CompanyWithPopulatedStructureSpec;

public interface CompanyWithPopulatedStructureService {
    void createCombinedCompany(CompanyWithPopulatedStructureSpec companySpec);
}
