package uk.gov.companieshouse.api.testdata.service.impl;

import uk.gov.companieshouse.api.testdata.service.RolesService;

import java.util.List;

public class RoleServiceImpl implements RolesService {
    @Override
    public boolean roleExists(String role) {
        return false;
    }

    @Override
    public void createRole(List<String> roles) {

    }

    @Override
    public void deleteRole(String role) {

    }
}
