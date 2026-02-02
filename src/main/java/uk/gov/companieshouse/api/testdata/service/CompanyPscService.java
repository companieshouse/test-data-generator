package uk.gov.companieshouse.api.testdata.service;

import java.util.List;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface CompanyPscService {
    List<CompanyPscs> create(CompanySpec companySpec) throws DataException;
    boolean delete(String id);
}
