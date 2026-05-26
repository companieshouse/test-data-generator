package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
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

    public IdentityVerificationServiceImpl(IdentityRepository identityRepository,
                                           UvidRepository uvidRepository,
                                           UserRepository userRepository) {
        this.identityRepository = identityRepository;
        this.uvidRepository = uvidRepository;
        this.userRepository = userRepository;
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
    public boolean deleteIdentityByEmail(String email) throws DataException, NoDataFoundException {

        if (email == null || email.isBlank()) {
            throw new DataException("email is required");
        }

        long deletedCount = identityRepository.deleteByEmail(email);

        if (deletedCount == 0) {
            throw new NoDataFoundException("No identity found for email: " + email);
        }

        Map<String, Object> logData = new HashMap<>();
        logData.put("email", email);
        LOG.debug("Identity deleted", logData);

        return true;
    }

    @Override
    public boolean deleteIdentityById(String identityId) throws DataException, NoDataFoundException {

        if (identityId == null || identityId.isBlank()) {
            throw new DataException("identityId is required");
        }

        if (!identityRepository.existsById(identityId)) {
            throw new NoDataFoundException("No identity found for identityId: " + identityId);
        }

        identityRepository.deleteById(identityId);

        Map<String, Object> logData = new HashMap<>();
        logData.put("identityId", identityId);
        LOG.debug("Identity deleted", logData);

        return true;
    }
}
