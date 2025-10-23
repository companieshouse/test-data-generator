package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.entity.User;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.UvidData;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.repository.UserRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class IdentityServiceImpl implements DataService<IdentityData, IdentitySpec> {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private IdentityRepository identityRepository;

    @Autowired
    private UvidRepository uvidRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RandomService randomService;

    @Override
    public IdentityData create(IdentitySpec identitySpec) {
        var identity = new Identity();
        identity.setId(UUID.randomUUID().toString());
        identity.setEmail(identitySpec.getEmail());
        identity.setCreated(getCurrentDateTime());
        identity.setStatus("VALID");
        identity.setUserId(identitySpec.getUserId());
        identity.setVerificationSource(identitySpec.getVerificationSource());
        identity.setSecureIndicator(false);
        identityRepository.save(identity);
        return new IdentityData(identity.getId());
    }

    public UvidData createIdentityWithUvid(IdentitySpec identitySpec) throws DataException {
        try {
            if (identitySpec.getUserId() == null || identitySpec.getUserId().isEmpty()) {
                throw new DataException("User ID is required");
            }
            if (identitySpec.getEmail() == null || identitySpec.getEmail().isEmpty()) {
                throw new DataException("Email is required");
            }

            String userId = identitySpec.getUserId();
            String email = identitySpec.getEmail();

            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                throw new DataException("User not found with ID: " + userId);
            }

            Optional<Identity> existingIdentityOpt = identityRepository.findByUserId(userId);
            if (existingIdentityOpt.isPresent()) {
                var existingIdentity = existingIdentityOpt.get();

                var existingUvid = uvidRepository.findByIdentityId(existingIdentity.getId());
                if (existingUvid != null) {
                    throw new DataException("User already has both identity and UVID");
                } else {
                    throw new DataException("User already has an identity but no UVID exists");
                }
            }

            Optional<Identity> existingEmailIdentityOpt = identityRepository.findByEmail(email);
            if (existingEmailIdentityOpt.isPresent()) {
                throw new DataException("Email is already associated with another identity");
            }

            var identity = new Identity();
            identity.setId(UUID.randomUUID().toString());
            identity.setEmail(email);
            identity.setCreated(getCurrentDateTime());
            identity.setStatus("VALID");
            identity.setUserId(userId);
            identity.setVerificationSource(identitySpec.getVerificationSource());
            identity.setSecureIndicator(false);
            identityRepository.save(identity);

            var uvid = new Uvid();
            uvid.setUvid(generateUvid());
            uvid.setType("PERMANENT");
            uvid.setIdentityId(identity.getId());
            uvid.setCreated(getCurrentDateTime());
            uvid = uvidRepository.save(uvid);

            return new UvidData(uvid.getId(), uvid.getUvid());

        } catch (DataException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new DataException("Failed to create identity and UVID", ex);
        }
    }

    @Override
    public boolean delete(String identityId) {
        var identity = identityRepository.findById(identityId);
        if (identity.isPresent()) {
            var existingUvid = uvidRepository.findByIdentityId(identityId);
            if (existingUvid != null) {
                uvidRepository.delete(existingUvid);
            }
            identityRepository.delete(identity.get());
            return true;
        }
        return false;
    }

    protected Instant getCurrentDateTime() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }

    private String generateUvid() {
        var uvid = new StringBuilder();
        for (var i = 0; i < 3; i++) {
            var randomChar = (char) ('A' + (randomService.getNumber(3) % 26));
            uvid.append(randomChar);
        }
        var randomDigit = (int) (randomService.getNumber(1) % 10);
        uvid.append(randomDigit);
        for (int i = 0; i < 2; i++) {
            char randomChar = (char) ('A' + (randomService.getNumber(2) % 26));
            uvid.append(randomChar);
        }
        uvid.append("22223");

        return uvid.toString();
    }
}