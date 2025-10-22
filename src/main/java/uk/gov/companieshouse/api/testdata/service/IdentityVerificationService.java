package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.identityverification.model.Identity;

public interface IdentityVerificationService {

    String createIdentityAndGetUvid(Identity identity)
            throws ApiErrorResponseException, URIValidationException;
}