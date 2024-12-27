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
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RandomService randomService;

    @Override
    public boolean userExists(String userId) {
        return repository.findById(userId).isPresent();
    }

    @Override
    public UserTestData create(UserSpec userSpec) throws DataException {
        var dateNow = LocalDate.now().atStartOfDay(ZONE_ID_UTC).toInstant();
        long timestamp = dateNow.toEpochMilli();
        final String password = userSpec.getPassword();
        if (password == null || password.isEmpty()) throw new DataException("Password is required to create a user");
        final var user = new Users();
        List<RoleSpec> roleList = userSpec.getRoles();
        if(roleList!=null){
            boolean invalidRole = roleList.stream().anyMatch(roleData -> roleData.getId() == null || roleData.getPermissions() == null || roleData.getPermissions().isEmpty());
            if (invalidRole) {
                throw new DataException("Role ID and permissions are required to create a role");
            }
            List<String> rolesList = new ArrayList<>();
            for (var roleData : roleList) {
                var role = new Roles();
                role.setId(roleData.getId()+"-playwright-role"+timestamp);
                role.setPermissions(roleData.getPermissions());
                rolesList.add(role.getId());
                roleRepository.save(role);
            }
            user.setRoles(rolesList);
        }
        String randomUser = "playwright-user" + timestamp + "@test.companieshouse.gov.uk";
        user.setId(randomService.getString(24));
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
        Users user = repository.findById(userId).orElseThrow(() -> new DataException("User id " + userId + " not found"));
        try {
            if (user.getRoles() != null) {
                user.getRoles().stream().map(roleRepository::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .forEach(roleRepository::delete);
            }
            repository.delete(user);
            if (userExists(userId)) {
                return false;
            }
        } catch (Exception e) {
            LOG.error("Failed to delete user", e);
            throw new DataException("Failed to delete user", e);
        }
        return repository.findById(userId).isEmpty();
    }
}
