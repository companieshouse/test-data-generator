package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.*;


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
}
