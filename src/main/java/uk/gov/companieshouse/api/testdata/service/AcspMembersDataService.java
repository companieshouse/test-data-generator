package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;

public interface AcspMembersDataService<T> {
    /**
     * Creates an ACSP member based on the provided specification.
     *
     * @param acspMembersSpec The specification for the ACSP member.
     * @return The created ACSP member entity.
     * @throws DataException If there is an issue creating the ACSP member.
     */
    T create(AcspMembersSpec acspMembersSpec) throws DataException;

    /**
     * Deletes information for the given {@code acspNumber}.
     *
     * @param acspNumber The unique ACSP number of the member to delete.
     * @return True if the data could be found and deleted, false otherwise.
     */
    boolean delete(String acspNumber);
}
