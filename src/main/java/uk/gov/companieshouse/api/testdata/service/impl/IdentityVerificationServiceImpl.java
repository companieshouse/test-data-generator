package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.repository.BacklogRepository;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class IdentityVerificationServiceImpl implements
        VerifiedIdentityService<IdentityVerificationResponse> {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final IdentityRepository identityRepository;
    private final UvidRepository uvidRepository;
    private final UserRepository userRepository;
    private final BacklogRepository backlogRepository;

    public IdentityVerificationServiceImpl(IdentityRepository identityRepository,
                                           UvidRepository uvidRepository,
                                           UserRepository userRepository, BacklogRepository backlogRepository) {
        this.identityRepository = identityRepository;
        this.uvidRepository = uvidRepository;
        this.userRepository = userRepository;
        this.backlogRepository = backlogRepository;
    }

    @Override
    public IdentityVerificationResponse getIdentityVerificationData(String email) {
        LOG.debug("getIdentityVerificationData called with email= "
                + email);

        if (email == null || email.isBlank()) {
            LOG.debug("Email is null or blank; returning null");
            return null;
        }

        Optional<Identity> identityOpt = identityRepository.findByEmail(email);
        if (identityOpt.isEmpty()) {
            LOG.debug("No identity found for email= " + email);
            return null;
        }

        var identity = identityOpt.get();
        LOG.debug("Found identity id= " + identity.getId() + " for email= " + email);

        var uvidOpt = uvidRepository.findByIdentityId(identity.getId());
        if (uvidOpt.isEmpty()) {
            LOG.debug("No UVID found for identityId= " + identity.getId());
            return null;
        }

        var uvid = uvidOpt.get();
        LOG.debug("Found UVID value= " + uvid.getValue() + " for identityId= " + identity.getId());

        String[] names = getUserNames(identity);

        var data = new IdentityVerificationResponse(
                identity.getId(),
                uvid.getValue(),
                names[0],
                names[1]
        );

        LOG.debug("Returning IdentityVerificationData for email "
                + email + " and identityId= " + identity.getId());
        return data;
    }

    private String[] getUserNames(Identity identity) {
        var firstName = "";
        var lastName = "";
        var userId = identity.getUserId();

        if (userId != null && !userId.isBlank()) {
            Optional<User> userOpt = userRepository.findById(userId);

            if (userOpt.isPresent()) {
                var user = userOpt.get();
                firstName = Optional.ofNullable(user.getForename()).orElse("");
                lastName = Optional.ofNullable(user.getSurname()).orElse("");
            } else {
                LOG.debug("User not found for userId= " + userId + "; falling back to empty names");
            }
        } else {
            LOG.debug("Identity has no userId; falling back to empty names");
        }

        return new String[]{firstName, lastName};
    }

    @Override
    public boolean deleteIdentityData(
            IdentityVerificationResponse identityVerificationResponse,
            String userId) {

        LOG.info("delete called for IdentityId " +  identityVerificationResponse.getIdentityId());

        try {
            LOG.info(String.valueOf(identityVerificationResponse));
            deleteIdentity(identityVerificationResponse, userId);
            return true;
        } catch (Exception ex) {
            LOG.error("Failed to delete identity data", ex);
            return false;
        }
    }

    private void deleteIdentity(IdentityVerificationResponse identityVerificationResponse, String userId) {

        try {
            uvidRepository.deleteByIdentityId(identityVerificationResponse.getIdentityId());
            LOG.debug("Deleted UVIDs for identityId={}");
        } catch (Exception ex) {
            LOG.error("Failed to delete UVIDs for identityId={}");
            throw ex;
        }

        executeDelete(
                () -> identityRepository.deleteById(identityVerificationResponse.getIdentityId()),
                "Deleted identity id={}",
                "Failed to delete identity id={}"
        );

        executeDelete(
                () -> backlogRepository.deleteByUserId(userId),
                "Deleted backlog for userId={}",
                "Failed to delete backlog for userId={}"
        );
    }

    private void executeDelete(Runnable deleteAction, String successMessage, String failureMessage) {
        try {
            deleteAction.run();
            LOG.debug(successMessage);
        } catch (Exception ex) {
            LOG.error(failureMessage, ex);
            throw ex;
        }
    }
}
