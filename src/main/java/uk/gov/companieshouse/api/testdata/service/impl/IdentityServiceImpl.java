package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.EncryptedDiscrepancyData;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.entity.Uvid;
import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.repository.UvidRepository;
import uk.gov.companieshouse.api.testdata.service.IdentityService;
import uk.gov.companieshouse.api.testdata.service.RandomService;

@Service
public class IdentityServiceImpl implements IdentityService {

    private final IdentityRepository identityRepository;
    private final UvidRepository uvidRepository;
    private final RandomService randomService;

    public IdentityServiceImpl(IdentityRepository identityRepository, UvidRepository uvidRepository, RandomService randomService) {
        this.identityRepository = identityRepository;
        this.uvidRepository = uvidRepository;
        this.randomService = randomService;
    }

    @Override
    public IdentityVerificationResponse createIdentity(IdentityVerificationRequest request) {
        String contextId = UUID.randomUUID().toString();

        Identity identity = new Identity();

        identity.setId(UUID.randomUUID().toString());
        identity.setCreated(Instant.now());
        identity.setVerificationSource(request.getVerificationSource());
        identity.setStatus(request.getStatus());
        identity.setAcspId(request.getAcspId());
        identity.setEmail(request.getEmail());
        identity.setAcspUserId(request.getAcspUserId());
        identity.setValidationMethod(request.getValidationMethod());
        identity.setAssuranceLevel(request.getAssuranceLevel());
        identity.setSecureIndicator(request.getSecureIndicator());
        identity.setSchemaVersion("1.0");
        identity.setStatusDate(Instant.now());
        identity.setVerificationDate(Instant.now().minus(365, ChronoUnit.DAYS));

        var encryptedDiscrepancyData = new EncryptedDiscrepancyData();
        encryptedDiscrepancyData.setCipherText("test-data");
        encryptedDiscrepancyData.setContextId(contextId);
        identity.setEncryptedIdentityData(encryptedDiscrepancyData);

        if (identityRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Identity already exists for email: " + request.getEmail());
        }

        identityRepository.save(identity);

        var uvid = new Uvid();
        uvid.setValue(randomService.getString(10).toUpperCase());
        uvid.setType("PERMANENT");
        uvid.setIdentityId(identity.getId());
        uvid.setCreated(Instant.now());
        uvidRepository.save(uvid);

        return new IdentityVerificationResponse(
                identity.getId(),
                uvid.getId(),
                null,
                null,
                identity.getAcspId(),
                identity.getAcspUserId());
    }

    @Override
    public IdentityVerificationResponse getIdentity(String identityId) {

        Optional<Identity> identity =
                identityRepository.findById(identityId);
        var uvid = uvidRepository.findByIdentityId(identityId);
        if (identity.isEmpty()) {
            return null;
        }

        return new IdentityVerificationResponse(
                identity.get().getId(),
                uvid.get().getId(),
                null,
                null,
                identity.get().getAcspId(),
                identity.get().getAcspUserId()
                );
    }

    @Override
    public boolean deleteIdentity(String identityId) {
        var identity = identityRepository.findById(identityId);
        if (identity.isEmpty()) {
            return false;
        }

        uvidRepository.deleteByIdentityId(identityId);
        identityRepository.deleteById(identityId);
        return true;
    }
}