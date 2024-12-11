package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;

public interface AcspProfileTestDataService {
    /**
     * Create ACSP profile data with the given {@code acspProfileSpec}
     *
     * @param acspProfileSpec The specification the new ACSP profile must adhere to
     * @return A {@link AcspProfileData}
     * @throws DataException If any error occurs
     */
    AcspProfileData createAcspProfileData(AcspProfileSpec acspProfileSpec) throws DataException;

    /**
     * Delete all data for ACSP profile with {@code acspNumber}
     *
     * @param acspNumber The ACSP number to be deleted
     * @throws DataException If any error occurs
     */
    void deleteAcspProfileData(String acspNumber) throws DataException;
}

