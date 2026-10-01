package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;

public interface IdentityService {
    IdentityVerificationResponse createIdentity(IdentityVerificationRequest request);

    IdentityVerificationResponse getIdentity(String identityId);

    boolean deleteIdentity(String identityId);
}