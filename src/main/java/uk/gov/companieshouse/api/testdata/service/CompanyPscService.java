package uk.gov.companieshouse.api.testdata.service;

import java.util.List;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;

public interface CompanyPscService {
    List<CompanyPscs> create(InternalCompanyRequest companySpec) throws DataException;
    boolean delete(String id);
}
