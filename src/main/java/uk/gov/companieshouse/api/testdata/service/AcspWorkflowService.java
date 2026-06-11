package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;

public interface AcspWorkflowService {
    /**
     * Creates a new acsp member test data based on the provided user specifications.
     *
     * @param acspMembersRequest the specifications of the acsp member to create
     * @return the created acsp members' test data
     * @throws DataException if there is an error during user creation
     */
    AcspMembersResponse createAcspMembersData(AcspMembersRequest acspMembersRequest) throws DataException;

    /**
     * Deletes an acsp members' test data by their membership id.
     *
     * @param acspMemberId the ID of the membership to delete
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteAcspMembersData(String acspMemberId) throws DataException;

    /**
     * Adds a new certificate test data based on the provided user specifications.
     *
     * @param certificatesRequest the specifications of the certificates to order
     * @return the created certificates test data
     * @throws DataException if there is an error during user creation
     */

    /**
     * Gets the ACSP profile data for a given ACSP number.
     *
     * @param acspNumber the ACSP number
     * @return the {@link AcspProfileResponse}
     * @throws NoDataFoundException if the profile cannot be found
     */
    Optional<AcspProfile> getAcspProfileData(String acspNumber)
            throws NoDataFoundException;

}
