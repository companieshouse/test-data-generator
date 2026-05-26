package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

public interface VerifiedIdentityService<T> {
    T getIdentityVerificationData(String email) throws DataException;

    boolean deleteIdentityByEmail(String email) throws DataException, NoDataFoundException;

    boolean deleteIdentityById(String identityId) throws DataException, NoDataFoundException;
}