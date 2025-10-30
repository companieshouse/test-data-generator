package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;

public interface VerifiedIdentityService<T> {
    T getIdentityVerificationData(String email) throws DataException;
}