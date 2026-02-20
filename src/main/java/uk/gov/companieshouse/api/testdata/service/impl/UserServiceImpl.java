package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.model.rest.enums.UserRoles;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private UserRepository repository;

    @Autowired
    private AdminPermissionsRepository adminPermissionsRepository;

    @Autowired
    private RandomService randomService;

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private UvidRepository uvidRepository;

    @Override
    @Transactional
    public UserResponse create(UserRequest userRequest) throws DataException {
        final String randomId = randomService.getString(23).toLowerCase();
        LOG.debug("randomService returned id= " + randomId);

        final String password = userRequest.getPassword();
        final var user = new User();

        if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
            user.setRoles(processRoles(userRequest.getRoles()));
        } else {
            LOG.debug("No roles provided for user creation");
        }

        String email = userRequest.getEmail() != null
                ? userRequest.getEmail() :
                "test-data-generated" + randomId + "@chtesttdg.mailosaur.net";

        user.setId(randomId);
        user.setEmail(email);
        user.setForename("Forename-" + randomId);
        user.setSurname("Surname-" + randomId);
        user.setLocale("GB_en");
        user.setPassword(password);
        user.setDirectLoginPrivilege(true);
        user.setCreated(getDateNow());
        user.setAdminUser(Optional.ofNullable(userRequest.getIsAdmin()).orElse(false));
        user.setTestData(true);

        if (userRequest.getIdentityVerification()
                != null && !userRequest.getIdentityVerification().isEmpty()) {
            user.setOneLoginUserId(user.getId());
        }

        repository.save(user);

        processIdentityVerifications(user, userRequest);

        LOG.info("User created successfully id= " + user.getId());
        return new UserResponse(user.getId(), user.getEmail(), user.getForename(), user.getSurname());
    }

    protected void processIdentityVerifications(User user, UserRequest userRequest) {
        if (userRequest.getIdentityVerification() == null
                || userRequest.getIdentityVerification().isEmpty()) {
            LOG.debug("No identity verification data provided");
            return;
        }

        LOG.debug("Creating identity verification entries: count= "
                + userRequest.getIdentityVerification().size());

        for (var identityVerificationSpec : userRequest.getIdentityVerification()) {

            if (identityVerificationSpec != null) {
                var verificationSource = identityVerificationSpec.getVerificationSource();

                if (verificationSource != null && !verificationSource.isEmpty()) {

                    LOG.debug("Creating identity for verificationSource= "
                            + verificationSource);
                    var identity = new Identity();
                    identity.setId(UUID.randomUUID().toString());
                    identity.setCreated(getDateNow());
                    identity.setStatus("VALID");
                    identity.setUserId(user.getId());
                    identity.setVerificationSource(verificationSource);
                    identity.setEmail(user.getEmail());
                    identity.setSecureIndicator(false);
                    identityRepository.save(identity);
                    LOG.info("Saved identity id = "
                            + identity.getId() + " for userId= " + user.getId());

                    var uvid = new Uvid();
                    uvid.setValue(randomService.getString(10).toUpperCase());
                    uvid.setType("PERMANENT");
                    uvid.setIdentityId(identity.getId());
                    uvid.setCreated(getDateNow());
                    uvidRepository.save(uvid);
                    LOG.info("Saved uvid = " + uvid.getValue()
                            + " for identityId= " + identity.getId());

                } else {
                    LOG.debug(
                            "Skipped identity verification entry "
                                    + "because verificationSource was null/empty");
                }
            } else {
                LOG.debug("Skipped null identityVerificationSpec");
            }
        }
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
                LOG.debug("Added entraGroupId " + entraGroupId + " for groupName= " + groupName);
            } else {
                throw new DataException("No entra_group_id found for group: " + groupName);
            }
        }
        return entraGroupIds;
    }

    UserRoles getUserRole(String roleName) throws DataException {
        LOG.debug("getUserRole called with roleName= " + roleName);
        if (roleName == null) {
            LOG.error("Invalid role name: null");
            throw new DataException("Invalid role name: null");
        }
        try {
            var role = UserRoles.valueOf(roleName);
            LOG.debug("Found UserRoles enum for roleName= " + roleName);
            return role;
        } catch (IllegalArgumentException error) {
            LOG.error("Invalid role name provided: "
                    + roleName);
            throw new DataException("Invalid role name: "
                    + roleName);
        }
    }

    @Override
    @Transactional
    public boolean delete(String userId) {
        LOG.info("delete called for userId= " + userId);
        var userOpt = repository.findById(userId);
        if (userOpt.isEmpty()) {
            LOG.debug("User not found for id=  " + userId);
            return false;
        }

        Optional<Identity> identityOpt = identityRepository.findByUserId(userId);
        if (identityOpt.isPresent()) {
            var identity = identityOpt.get();
            LOG.info("Found identity for userId = "
                    + userId
                    + "Identity_id = " + identity.getId());
            try {
                uvidRepository.deleteByIdentityId(identity.getId());
                LOG.debug("Deleted UVIDs for identityId = " + identity.getId());
            } catch (Exception ex) {
                LOG.error("Failed to delete UVIDs for identityId = "
                        + identity.getId() + ex.getMessage());
                throw ex;
            }

            try {
                identityRepository.delete(identity);
                LOG.debug("Deleted identity id= " + identity.getId());
            } catch (Exception ex) {
                LOG.error("Failed to delete identity id= "
                        + identity.getId() +  ex.getMessage());
                throw ex;
            }
        } else {
            LOG.debug("No identity associated with userId= " + userId);
        }

        try {
            repository.delete(userOpt.get());
            LOG.info("Deleted user id= " + userId);
        } catch (Exception ex) {
            LOG.error("Failed to delete user id " + userId, ex);
            throw ex;
        }
        return true;
    }

    @Override
    public Optional<User> getUserById(String userId) {
        LOG.debug("getUserById called for userId= " + userId);
        var result = repository.findById(userId);
        LOG.debug("getUserById found={}");
        return result;
    }

    protected Instant getDateNow() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }
}