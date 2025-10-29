package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;

public interface VerifiedIdentityService<I> {
    I getIdentityVerificationData(String email) throws DataException;
}