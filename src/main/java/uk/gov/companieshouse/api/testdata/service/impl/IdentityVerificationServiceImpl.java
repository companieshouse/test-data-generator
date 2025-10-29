package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationData;
import uk.gov.companieshouse.api.testdata.model.rest.VerifiedIdentitySpec;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class IdentityVerificationServiceImpl implements VerifiedIdentityService<IdentityVerificationData, VerifiedIdentitySpec> {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private IdentityRepository repository;


    @Autowired
    private UvidRepository uvidRepository;

    /**
     * Finds an identity by email, then finds the associated UVID.
     *
     * @param spec The request object containing the email.
     * @return An IdentityVerificationData (identity_id and uvid)
     * if both are found, otherwise null.
     */
    @Override
    public IdentityVerificationData getIdentityVerificationData(VerifiedIdentitySpec spec) {
        Optional<Identity> identityOpt = repository.findByEmail(spec.getEmail());

        if (identityOpt.isEmpty()) {
            return null;
        }

        Identity identity = identityOpt.get();
        return uvidRepository.findByIdentityId(identity.getId())
                .map(uvid -> new IdentityVerificationData(identity.getId(), uvid.getUvid()))
                .orElse(null);
    }

    protected Instant getCurrentDateTime() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }
}