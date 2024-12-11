package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;

public interface AcspMembersService extends AcspMembersDataService<AcspMembers> {

    /**
     * Checks whether an ACSP member with the given {@code acspNumber} exists.
     *
     * @param acspNumber The unique ACSP number of the member.
     * @return True if the member exists, false otherwise.
     */
    boolean memberExists(String acspNumber);
}

