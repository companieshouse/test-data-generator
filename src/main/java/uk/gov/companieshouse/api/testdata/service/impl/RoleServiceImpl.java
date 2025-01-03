package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public RoleData create(RoleSpec roleSpec) {
        var role = new Roles();
        role.setId(roleSpec.getId());
        role.setPermissions(roleSpec.getPermissions());
        roleRepository.save(role);
        return new RoleData(role.getId());
    }

    @Override
    public boolean delete(String roleId) {
        if (!roleRepository.existsById(roleId)) {
            return false;
        }
        roleRepository.deleteById(roleId);
        return true;
    }
}
