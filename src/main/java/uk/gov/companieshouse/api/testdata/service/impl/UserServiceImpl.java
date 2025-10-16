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

    private final UserRepository repository;

    private final AdminPermissionsRepository adminPermissionsRepository;

    private final RandomService randomService;

    @Autowired
    public UserServiceImpl(UserRepository repository, AdminPermissionsRepository adminPermissionsRepository,
            RandomService randomService) {
        super();
        this.repository = repository;
        this.adminPermissionsRepository = adminPermissionsRepository;
        this.randomService = randomService;
    }

    @Override
    public UserData create(UserSpec userSpec) throws DataException {
        var randomId = randomService.getString(23).toLowerCase();
        final String password = userSpec.getPassword();
        final var user = new User();

        if (userSpec.getRoles() != null && !userSpec.getRoles().isEmpty()) {
            user.setRoles(processRoles(userSpec.getRoles()));
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

    List<String> processRoles(List<String> roles) throws DataException {
        List<String> entraGroupIds = new ArrayList<>();
        for (String roleName : roles) {
            var userRole = getUserRole(roleName);
            String groupName = userRole.getGroupName();
            var adminPermissionEntity = adminPermissionsRepository.findByGroupName(groupName);
            if (adminPermissionEntity == null) {
                throw new DataException("No admin permissions found for groupName: "
                        + groupName + " (role: " + roleName + ")");
            }
            String entraGroupId = adminPermissionEntity.getEntraGroupId();
            if (entraGroupId != null && !entraGroupId.isEmpty()) {
                entraGroupIds.add(entraGroupId);
            } else {
                throw new DataException("No entra_group_id found for group: " + groupName);
            }
        }
        return entraGroupIds;
    }

    UserRoles getUserRole(String roleName) throws DataException {
        if (roleName == null) {
            throw new DataException("Invalid role name: null");
        }
        try {
            return UserRoles.valueOf(roleName);
        } catch (IllegalArgumentException error) {
            throw new DataException("Invalid role name: " + roleName);
        }
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