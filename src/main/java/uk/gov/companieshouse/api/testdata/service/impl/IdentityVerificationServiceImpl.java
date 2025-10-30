package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationData;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;

@Service
public class IdentityVerificationServiceImpl implements
        VerifiedIdentityService<IdentityVerificationData> {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private IdentityRepository repository;

    @Autowired
    private UvidRepository uvidRepository;

    @Override
    public IdentityVerificationData getIdentityVerificationData(String email) {

        Optional<Identity> identityOpt = repository.findByEmail(email);

        if (identityOpt.isEmpty()) {
            return null;
        }

        var identity = identityOpt.get();
        return uvidRepository.findByIdentityId(identity.getId())
                .map(uvid -> new IdentityVerificationData(identity.getId(), uvid.getValue()))
                .orElse(null);
    }

    protected Instant getCurrentDateTime() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }
}