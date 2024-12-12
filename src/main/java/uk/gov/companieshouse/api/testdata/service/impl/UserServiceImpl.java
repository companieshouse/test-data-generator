package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RolesService;
import uk.gov.companieshouse.api.testdata.service.UserService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserServiceImpl implements UserService, RolesService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    private static final String ROLE_STEM = "/users/";

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean userExits(String userId) {
        Optional<Users> existingUser = repository.findById(userId);
        return existingUser.isPresent();
    }


    @Override
    public UserTestData creteUser(UsersSpec usersSpec) throws DataException {
        LocalDate now = LocalDate.now();
        Instant dateNow = now.atStartOfDay(ZONE_ID_UTC).toInstant();
        final String password = usersSpec.getPassword();

        final List<String> roleList = usersSpec.getRoles();
        final Users user = new Users();

        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
//
//        if (roleList != null && !roleList.isEmpty()) {
//            createRole(roleList);
//        }
//        else{
//            throw new DataException("Role does not exist");
//        }
//        user.setRoles(roleList);
        long timestamp = System.currentTimeMillis();
        String randomUser = "playwright-user" + timestamp + "@test.companieshouse.gov.uk";
        user.setId(generateRandomString(24));
        user.setEmail(randomUser);
        user.setForename("Forename-"+timestamp);
        user.setSurname("Surname-"+timestamp);
        user.setLocale("GB_en");
        user.setPassword(password);
        user.setDirectLoginPrivilege(true);
        user.setCreated(dateNow);
        repository.save(user);
        return new UserTestData(user.getId(), user.getEmail(), user.getForename(), user.getSurname());
    }

    @Override
    public void deleteUser(String userId) {

    }

    @Override
    public boolean roleExists(String role) {
        Optional<Roles> existingRole = roleRepository.findById(role);
        return existingRole.isPresent();
    }

    @Override
    public void createRole(List<String> roleList) throws DataException {
        for (final String role : roleList) {
            if (!roleExists(role)) {
                throw new DataException("Role does not exist");
            }
            // Initialise the Role structure
            Roles newRole = new Roles();
            newRole.setPermissions(roleList);
            String randomRoleId = generateRandomString(8);
            newRole.setId(role + "-playwright-role" + randomRoleId);
            roleRepository.save(newRole);
        }
    }

    private String generateRandomString(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, length);
    }

    @Override
    public void deleteRole(String role) {

    }
}
