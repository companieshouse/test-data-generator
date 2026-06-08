package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;

public interface CompanyCreationOrchestratorService {

    CompanyProfileResponse createPublicCompany(PublicCompanyRequest companySpec) throws DataException;

    CompanyProfileResponse createInternalCompany(CompanyRequest companySpec) throws DataException;

    PopulatedCompanyDetailsResponse buildCompanyDataStructure(CompanyRequest spec)
            throws DataException;

    CompanyProfileResponse persistCompanyDataStructure(CompanyWithPopulatedStructureRequest companySpec)
            throws DataException;
}

