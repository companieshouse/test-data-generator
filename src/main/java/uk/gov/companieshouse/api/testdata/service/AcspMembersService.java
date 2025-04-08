package uk.gov.companieshouse.api.testdata.service;

import java.util.List;

import uk.gov.companieshouse.api.testdata.model.entity.AcspMembers;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;

public interface AcspMembersService extends DataService<AcspMembersData, AcspMembersSpec> {

    /**
     * Retrieves ACSP memberships associated with the given user_id.
     *
     * @param userId the user_id of the user whose acsp membership
     *               is to be retrieved
     * @return a List containing the acsp memberships if found
     *          or empty if not found
     */
    List<AcspMembers> findAllByUserId(String userId);

    /**
     * Deletes the ACSP memberships associated with the given user_id.
     * @param userId the user_id of the user whose acsp membership
     *               is to be deleted
     * @return true if the acsp membership was deleted, false otherwise
     */
    boolean deleteByUserId(String userId);
}
