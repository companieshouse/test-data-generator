package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.rest.request.IdentityVerificationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityResponse;

public interface IdentityService {

    IdentityResponse createIdentity(
            IdentityVerificationRequest request);

    IdentityResponse getIdentity(
            String identityId);

    boolean deleteIdentity(
            String identityId);
}