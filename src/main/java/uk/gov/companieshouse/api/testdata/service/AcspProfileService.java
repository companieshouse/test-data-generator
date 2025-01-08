package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;

public interface AcspProfileService extends DataService<AcspProfile, AcspSpec> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     *
     * @param acspNumber
     * @return True if a company exists. False otherwise
     */
    boolean acspProfileExists(long acspNumber);

}
