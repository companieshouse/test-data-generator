package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.model.entity.EncryptedDiscrepancyData;
import uk.gov.companieshouse.api.testdata.model.entity.Identity;
import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityResponse;
import uk.gov.companieshouse.api.testdata.repository.IdentityRepository;
import uk.gov.companieshouse.api.testdata.service.IdentityService;

@Service
public class IdentityServiceImpl implements IdentityService {

    private final IdentityRepository identityRepository;

    public IdentityServiceImpl(IdentityRepository identityRepository) {
        this.identityRepository = identityRepository;
    }

    @Override
    public IdentityResponse createIdentity(
            IdentityVerificationRequest request) {
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

        return new IdentityResponse(
                identity.getId(),
                identity.getAcspId(),
                identity.getAcspUserId());
    }

    @Override
    public IdentityResponse getIdentity(String identityId) {

        Optional<Identity> identity =
                identityRepository.findById(identityId);

        if (identity.isEmpty()) {
            return null;
        }

        return new IdentityResponse(
                identity.get().getId(),
                identity.get().getAcspId(),
                identity.get().getAcspUserId());
    }

    @Override
    public boolean deleteIdentity(String identityId) {

        if (!identityRepository.existsById(identityId)) {
            return false;
        }

        identityRepository.deleteById(identityId);

        return true;
    }
}