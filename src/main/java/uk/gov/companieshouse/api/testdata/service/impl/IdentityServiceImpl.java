package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvIdRepository;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class IdentityServiceImpl implements DataService<IdentityData, IdentitySpec> {
    private static final ZoneId ZONE_ID_UTC = ZoneId.of("UTC");
    private static final Logger LOG =
            LoggerFactory.getLogger(String.valueOf(IdentityServiceImpl.class));

    @Autowired
    private IdentityRepository repository;

    @Autowired
    private RandomService randomService;

    @Autowired
    private UvIdRepository uvIdRepository;

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
        identity.setSecureIndicator(false);
        repository.save(identity);
        if (identitySpec.getUvId() != null) {
            createUvId(identitySpec.getUvId(), identity.getId());
        }
        return new IdentityData(identity.getId());
    }

    @Override
    public boolean delete(String identityId) {
        deleteUvId(identityId);
        var identity = repository.findById(identityId);
        identity.ifPresent(repository::delete);
        return identity.isPresent();
    }

    private void deleteUvId(String identityId) {
        uvIdRepository.deleteByIdentityId(identityId);
    }

    protected Instant getCurrentDateTime() {
        return LocalDateTime.now(ZONE_ID_UTC).toInstant(ZoneOffset.UTC);
    }

    private void createUvId(String uvId, String identityId) {
        var randomId = randomService.generateId();
        var uvIdEntity = new Uvid();
        uvIdEntity.setId(randomId);
        uvIdEntity.setIdentityId(identityId);
        uvIdEntity.setUvid(uvId);
        uvIdEntity.setType("PERMANENT");
        uvIdEntity.setCreated(getCurrentDateTime());
        uvIdRepository.save(uvIdEntity);
    }
}
