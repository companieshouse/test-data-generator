package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

public interface TestDataService {
    /**
     * Create company data with given {@code companySpec}.
     *
     * @param companySpec The specification the new company must adhere to
     * @return A {@link CompanyData}
     * @throws DataException If any error occurs
     */
    CompanyData createCompanyData(CompanySpec companySpec) throws DataException;

    /**
     * Delete all data for company {@code companyNumber}.
     *
     * @param companyNumber The company number to be deleted
     * @throws DataException If any error occurs
     */
    void deleteCompanyData(String companyNumber) throws DataException;

    /**
     * Creates a new user test data based on the provided user specifications.
     *
     * @param userSpec the specifications of the user to create
     * @return the created user's test data
     * @throws DataException if there is an error during user creation
     */
    UserData createUserData(UserSpec userSpec) throws DataException;

    /**
     * Deletes a user test data by their user ID.
     *
     * @param userId the ID of the user to delete
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteUserData(String userId) throws DataException;

    /**
     * Creates a new identity test data based on the provided identity specifications.
     *
     * @param identitySpec the specifications of the identity to create
     * @return the created identity's test data
     * @throws DataException if there is an error during identity creation
     */
    IdentityData createIdentityData(IdentitySpec identitySpec) throws DataException;

    /**
     * Deletes an identity test data by their identity ID.
     *
     * @param identityId the ID of the identity to delete
     * @throws DataException if there is an error during identity deletion
     */
    boolean deleteIdentityData(String identityId) throws DataException;

    /**
     * Creates a new acsp member test data based on the provided user specifications.
     *
     * @param acspMembersSpec the specifications of the acsp member to create
     * @return the created acsp members' test data
     * @throws DataException if there is an error during user creation
     */
    AcspMembersData createAcspMembersData(AcspMembersSpec acspMembersSpec) throws DataException;

    /**
     * Deletes an acsp members' test data by their acsp membership id.
     *
     * @param acspMemberId the ID of the profile to delete
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteAcspMembersData(String acspMemberId) throws DataException;

    /**
     * Deletes all appeals data for a given penalty reference and company number.
     *
     * @param companyNumber the company number
     * @param penaltyReference the penalty reference
     * @return true if the entity was deleted, false otherwise
     * @throws DataException if there is an error during deletion
     */
    boolean deleteAppealsData(String companyNumber, String penaltyReference) throws DataException;

    /**
     * Deletes acsp memberships by their user_id.
     *
     * @param userId user_id of the acsp membership to delete
     * @return true if acsp membership was deleted, false otherwise
     * @throws DataException if there is an error during deletion
     */
    boolean deleteAcspMemberDataByUserId(String userId) throws DataException;
}
