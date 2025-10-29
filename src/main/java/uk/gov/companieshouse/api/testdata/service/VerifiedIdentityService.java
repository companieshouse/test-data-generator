package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityVerificationData;
import uk.gov.companieshouse.api.testdata.model.rest.VerifiedIdentitySpec;

public interface VerifiedIdentityService<I, V> {
     IdentityVerificationData getIdentityVerificationData(VerifiedIdentitySpec spec)  throws DataException;

}