package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;

import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;

public interface AcspMembersService extends DataService<AcspMembersData, AcspMembersSpec> {
    /**
     * Retrieves the data associated with a given user ID.
     *
     * @param acspMembersId the ID of the acsp member whose data are to be retrieved
     * @return an Optional containing the acsp member if found, or empty if not found
     */
    Optional<AcspMembers> getAcspMembersById(String acspMembersId);
}
