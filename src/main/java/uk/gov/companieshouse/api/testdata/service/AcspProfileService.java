package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;

public interface AcspProfileService {

    AcspProfileData create() throws DataException;

    boolean delete(String acspNumber);
}
