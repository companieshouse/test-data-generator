package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import java.util.List;

public interface CompanyPscsService {
    List<CompanyPscs> create(CompanySpec companySpec) throws DataException;
    boolean delete(String id);
}
