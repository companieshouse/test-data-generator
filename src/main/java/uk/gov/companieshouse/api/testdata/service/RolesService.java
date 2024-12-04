package uk.gov.companieshouse.api.testdata.service;

import java.util.List;

public interface RolesService {
    boolean roleExists(String role);
    void createRole(List<String> roles);
    void deleteRole(String role);
}
