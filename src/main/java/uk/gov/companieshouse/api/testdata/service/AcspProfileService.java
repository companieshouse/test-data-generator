package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;

public interface AcspProfileService extends DataService<AcspProfile> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     *
     * @param acspNumber
     * @return True if a company exists. False otherwise
     */
    boolean acspProfileExists(long acspNumber);

}
