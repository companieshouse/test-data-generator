package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;

public interface CreateCompanyWorkflowService {

    CompanyProfileResponse createPublicCompany(PublicCompanyRequest companySpec) throws DataException;

    CompanyProfileResponse createInternalCompany(InternalCompanyRequest companySpec) throws DataException;

    PopulatedCompanyDetailsResponse buildCompanyDataStructure(InternalCompanyRequest spec)
            throws DataException;

    CompanyProfileResponse persistCompanyDataStructure(CompanyWithPopulatedStructureRequest companySpec)
            throws DataException;
}


