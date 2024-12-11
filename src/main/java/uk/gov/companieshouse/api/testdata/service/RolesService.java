package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;

import java.util.List;

public interface RolesService {
    boolean roleExists(String role);
    void createRole(List<String> roles) throws DataException;
    void deleteRole(String role);
}
