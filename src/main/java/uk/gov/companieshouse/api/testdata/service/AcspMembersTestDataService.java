package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;

public interface AcspMembersTestDataService {
    /**
     * Create ACSP member data with the given {@code acspMembersSpec}.
     *
     * @param acspMembersSpec The specification the new ACSP member must adhere to.
     * @return A {@link AcspMembersData}.
     * @throws DataException If any error occurs during creation.
     */
    AcspMembersData createAcspMembersData(AcspMembersSpec acspMembersSpec) throws DataException;

    /**
     * Delete all data for the ACSP member identified by {@code acspNumber}.
     *
     * @param acspNumber The ACSP number of the member to be deleted.
     * @throws DataException If any error occurs during deletion.
     */
    void deleteAcspMembersData(String acspNumber) throws DataException;
}

