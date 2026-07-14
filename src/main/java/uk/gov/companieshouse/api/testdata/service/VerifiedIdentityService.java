package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;

public interface VerifiedIdentityService<T> {
    T getIdentityVerificationData(String email) throws DataException;

    /**
     * Deletes the Identity, Uvid and Backlog associated with a given identity email and user Id.
     *
     * @param identityVerificationResponse the identityVerificationResponse of the identity whose identity, Uvid are to be deleted
     * @param userId the user id of the user whose backlog to be deleted
     * @return true if the user was deleted, false otherwise
     */
     boolean deleteIdentityData(IdentityVerificationResponse identityVerificationResponse, String userId);
}