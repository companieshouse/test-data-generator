package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Roles;
import uk.gov.companieshouse.api.testdata.model.entity.Users;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserTestData;
import uk.gov.companieshouse.api.testdata.repository.RoleRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public UserTestData create(UserSpec userSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = dateNow.toEpochMilli();
        final String password = userSpec.getPassword();
        final var user = new Users();
        List<RoleSpec> roleList = userSpec.getRoles();
        if(roleList!=null){
            var role = new Roles();
            List<String> rolesList = new ArrayList<>();
            for (var roleData : roleList) {
                if (roleData.getId() == null || roleData.getPermissions() == null) {
                    throw new DataException("Role ID and permissions are required to create a role");
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
    public boolean delete(String userId) throws DataException {
        try {
            if (userExists(userId)) {
                Users user = repository.findById(userId).orElseThrow(() -> new DataException("User not found"));
                if (user.getRoles() != null) {
                    for (String roleId : user.getRoles()) {
                        Optional<Roles> existingRole = roleRepository.findById(roleId);
                        existingRole.ifPresent(roleRepository::delete);
                    }
                }
                repository.delete(user);
            }
            else {
                LOG.error("User id " + userId + " not found");
                throw new DataException("User id " + userId + " not found");
            }
        }
        catch (Exception e) {
            LOG.error("Failed to delete user");
            throw new DataException("Failed to delete user", e);
        }
        return repository.findById(userId).isPresent();
    }

    private String generateRandomString(int length) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return uuid.substring(0, length);
    }
}
