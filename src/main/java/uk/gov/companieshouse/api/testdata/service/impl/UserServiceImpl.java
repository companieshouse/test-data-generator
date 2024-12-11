package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import uk.gov.companieshouse.api.testdata.exception.DataException;
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
import java.util.Random;


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
    public void creteUser(RolesSpec rolesSpec) throws DataException {
        LocalDate now = LocalDate.now();
        final List<String> roleList = rolesSpec.getRoles();
        final Users user = new Users();

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        if (roleList != null && !roleList.isEmpty()) {
            createRole(roleList);
        }
        else{
            throw new DataException("Role does not exist");
        }
        user.setRoles(roleList);
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public boolean roleExists(String role) {
        return roleRepository.findByRoleId(role).isPresent();
    }

    @Override
    public void createRole(List<String> roleList) throws DataException {
        for (final String role : roleList) {
            if (!roleExists(role)) {
                throw new DataException("Role does not exist");
            }
            // Initialise the Role structure
            Role newRole = new Role();
            newRole.setPermissions(roleList);
            String randomRoleId = generateRandomString(8, "numeric");
            newRole.setId(roleList.get(i) + "-playwright-role" + randomRoleId);
            roleRepository.save(newRole);
        }
    }

    private String generateRandomString(int length, String charset) {
        StringBuilder randomString = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            randomString.append(charset.charAt(random.nextInt(charset.length())));
        }
        return randomString.toString();
    }

    @Override
    public void deleteRole(String role) {

    }
}
