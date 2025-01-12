package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;

public interface AcspProfileService extends DataService<AcspProfileData, AcspProfileSpec> {
    /**
     * Retrieves the data associated with a given profile ID.
     *
     * @param acspProfileId the ID of the acsp profile whose data are to be retrieved
     * @return an Optional containing the profile if found, or empty if not found
     */
    Optional<AcspProfile> getAcspProfileById(String acspProfileId);
}
