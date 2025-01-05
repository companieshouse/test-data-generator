package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Role;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;

@Service
public class RoleServiceImpl implements DataService<RoleData, RoleSpec> {

    @Autowired
    private RoleRepository repository;

    @Override
    public RoleData create(RoleSpec roleSpec) {
        var role = new Role();
        role.setId(roleSpec.getId());
        role.setPermissions(roleSpec.getPermissions());
        repository.save(role);
        return new RoleData(role.getId());
    }

    @Override
    public boolean delete(String roleId) {
        var role = repository.findById(roleId);
        role.ifPresent(repository::delete);
        return role.isPresent();
    }
}
