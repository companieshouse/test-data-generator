package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;

public interface RoleService {

        void create(RoleSpec roleSpec) throws DataException;

        boolean delete(String roleId) throws DataException;
}
