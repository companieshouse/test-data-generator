package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class IdentityServiceImpl implements DataService<IdentityData,IdentitySpec> {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");

    @Autowired
    private IdentityRepository repository;

    @Autowired
    private RandomService randomService;

    @Override
    public IdentityData create(IdentitySpec identitySpec) {
        var randomId = randomService.getString(24).toLowerCase();
        var identity = new Identity();
        identity.setId(randomId);
        identity.setEmail(identitySpec.getEmail());
        identity.setCreated(getCurrentDateTime());
        identity.setStatus("VALID");
        identity.setUserId(identitySpec.getUserId());
        identity.setVerificationSource(identitySpec.getVerificationSource());
        repository.save(identity);
        return new IdentityData(identity.getId());
    }

    @Override
    public boolean delete(String identityId) {
        var identity = repository.findById(identityId);
        identity.ifPresent(repository::delete);
        return identity.isPresent();
    }

    protected Instant getCurrentDateTime() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }
}
