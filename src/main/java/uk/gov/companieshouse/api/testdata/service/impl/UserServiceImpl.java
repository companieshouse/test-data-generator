package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserRoles;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private UserRepository repository;

    @Autowired
    private AdminPermissionsRepository adminPermissionsRepository;

    @Autowired
    private RandomService randomService;

    @Override
    public UserData create(UserSpec userSpec) throws DataException {
        var randomId = randomService.getString(23).toLowerCase();
        final String password = userSpec.getPassword();
        final var user = new User();

        if (userSpec.getRoles() != null && !userSpec.getRoles().isEmpty()) {
            List<String> entraGroupIds = new ArrayList<>();
            for (String roleName : userSpec.getRoles()) {
                try {
                    var userRole = UserRoles.valueOf(roleName);
                    String entraGroupId = userRole.getEntraGroupId();

                    var adminPermissionEntity =
                            adminPermissionsRepository.findByEntraGroupId(entraGroupId);
                    if (adminPermissionEntity == null) {
                        throw new DataException("No admin permissions found for entraGroupId: "
                                + entraGroupId + " (role: " + roleName + ")");
                    }

                    entraGroupIds.add(entraGroupId);
                } catch (IllegalArgumentException error) {
                    throw new DataException("Invalid role name: " + roleName);
                }
            }
            user.setRoles(entraGroupIds);
        }

        String email = userSpec.getEmail() != null ? userSpec.getEmail() :
                "test-data-generated" + randomId + "@chtesttdg.mailosaur.net";

        user.setId(randomId);
        user.setEmail(email);
        user.setForename("Forename-" + randomId);
        user.setSurname("Surname-" + randomId);
        user.setLocale("GB_en");
        user.setPassword(password);
        user.setDirectLoginPrivilege(true);
        user.setCreated(getDateNow());
        user.setAdminUser(Optional.ofNullable(userSpec.getIsAdmin()).orElse(false));
        user.setTestData(true);
        repository.save(user);
        return new UserData(user.getId(), user.getEmail(), user.getForename(), user.getSurname());
    }

    @Override
    public boolean delete(String userId) {
        var user = repository.findById(userId);
        user.ifPresent(repository::delete);
        return user.isPresent();
    }

    public void updateUserWithOneLogin(String userId) {
        var user = repository.findById(userId);
        if (user.isPresent()) {
            var existingUser = user.get();
            existingUser.setOneLoginUserId(userId);
            repository.save(existingUser);
        }
    }

    @Override
    public Optional<User> getUserById(String userId) {
        return repository.findById(userId);
    }

    protected Instant getDateNow() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }
}