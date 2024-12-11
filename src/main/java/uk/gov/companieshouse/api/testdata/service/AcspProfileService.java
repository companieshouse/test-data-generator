package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;

public interface AcspProfileService extends DataService<AcspProfile> {

    /**
     * Creates a new ACSP profile based on the provided specification and returns the persisted entity.
     *
     * @param spec specification for the new ACSP profile
     * @return the created AcspProfile entity
     */
    AcspProfile create(AcspProfileSpec spec);

    /**
     * Checks whether an ACSP profile exists for the given ACSP number.
     *
     * @param acspNumber the ACSP number to check
     * @return true if an ACSP profile exists, false otherwise
     */
    boolean profileExists(String acspNumber);

    /**
     * Checks whether an ACSP with the given {@code acspNumber} is present.
     *
     * @param acspNumber the ACSP number to check
     * @return true if an ACSP exists, false otherwise
     */
    boolean acspExists(String acspNumber);

    /**
     * Deletes ACSP data for the given ACSP number.
     *
     * @param acspNumber the ACSP number to delete
     */
    void deleteAcspData(String acspNumber);

    /**
     * Creates a new ACSP profile based on the provided specification and returns
     * the business representation (AcspData) of the created profile.
     *
     * @param spec specification for the new ACSP profile
     * @return an AcspData object representing the created ACSP
     */
    AcspProfileData createAcspData(AcspProfileSpec spec);
}
