package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.authentication.Permissions;
import uk.gov.companieshouse.api.testdata.model.authentication.RoleTypes;
import uk.gov.companieshouse.api.testdata.model.entity.Role;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RolesSpec;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RolesService;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static uk.gov.companieshouse.api.testdata.model.authentication.Permissions.permissions;

public class UserServiceImpl implements UserService, RolesService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private static final String ROLE_STEM = "/users/";

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean userExits(String userId) {
        return repository.findByUserId(userId).isPresent();
    }


    @Override
    public void creteUser(RolesSpec roles) throws DataException {
        LocalDate now = LocalDate.now();
        final List<String> roleList = roles.getRoles();
        final Users user = new Users();
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        if (roleList != null && !roleList.isEmpty()) {
            for (final String role : roleList) {
                if (!roleExists(role)) {
                    throw new DataException("Role does not exist");
                }
                // Initialise the Role structure
                Role newRole = new Role();
                List<String> permissionList = Permissions.getPermissions(RoleTypes.valueOf(role));
                newRole.setPermissions(permissionList);
                newRole.setId(role);
            }
        }
        else{
            throw new DataException("Role does not exist");
        }
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public boolean roleExists(String role) {
        return roleRepository.findByRoleId(role).isPresent();
    }

    @Override
    public void createRole(List<String> roles) {

    }

    @Override
    public void deleteRole(String role) {

    }
}
