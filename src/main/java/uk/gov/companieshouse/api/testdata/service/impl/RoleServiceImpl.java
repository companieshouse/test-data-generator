package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void create(RoleSpec roleSpec) throws DataException {
        var role = new Roles();
        role.setId(roleSpec.getId());
        role.setPermissions(roleSpec.getPermissions());
        roleRepository.save(role);
    }

    @Override
    public boolean delete(String roleId) throws DataException {
        if (!roleRepository.existsById(roleId)) {
            throw new DataException("Role not found");
        }
        roleRepository.deleteById(roleId);
        return true;
    }
}
