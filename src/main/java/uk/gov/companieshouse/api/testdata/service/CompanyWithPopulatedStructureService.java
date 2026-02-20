package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;

public interface CompanyWithPopulatedStructureService {
    void createCompanyWithPopulatedStructure(CompanyWithPopulatedStructureRequest companySpec);
}
