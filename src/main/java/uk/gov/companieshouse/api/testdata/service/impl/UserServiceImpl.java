package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RolesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UsersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public boolean userExists(String userId) {
        Optional<Users> existingUser = repository.findById(userId);
        return existingUser.isPresent();
    }

    @Override
    public UserTestData createUser(UsersSpec usersSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = System.currentTimeMillis();
        final String password = usersSpec.getPassword();
        final var user = new Users();
        List<RolesSpec> roleList = usersSpec.getRoles();
        if(roleList!=null){
            var role = new Roles();
            List<String> rolesList = new ArrayList<>();
            for (var roleData : roleList) {
                if (roleData.getId() == null || roleData.getPermissions() == null) {
                    throw new DataException("Role does not exist");
                }
                role.setId(roleData.getId()+"-playwright-role"+timestamp);
                role.setPermissions(roleData.getPermissions());
                rolesList.add(role.getId());
                roleRepository.save(role);
            }
            user.setRoles(rolesList);
        }
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
    public void deleteUser(String userId) throws DataException {
        try {
            Optional<Users> existingUser = repository.findById(userId);
            if (existingUser.isPresent()) {
                Users user = existingUser.get();
                if (user.getRoles() != null) {
                    for (String roleId : user.getRoles()) {
                        Optional<Roles> existingRole = roleRepository.findById(roleId);
                        existingRole.ifPresent(roleRepository::delete);
                    }
                }
                existingUser.ifPresent(repository::delete);
            }
            else {
                LOG.error("User id " + userId + " not found");
                throw new DataException("User id " + userId + " not found");
            }
        }
        catch (Exception e) {
            LOG.error("Failed to delete user");
            throw new DataException("Failed to delete user");
        }
    }

    private String generateRandomString(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, length);
    }
}
