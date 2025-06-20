package uk.gov.companieshouse.api.testdata.service;

import java.util.List;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltyData;
import uk.gov.companieshouse.api.testdata.model.rest.PostcodesData;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
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
     * Deletes an acsp members' test data by their membership id.
     *
     * @param acspMemberId the ID of the membership to delete
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteAcspMembersData(String acspMemberId) throws DataException;

    /**
     * Adds a new certificate test data based on the provided user specifications.
     *
     * @param certificatesSpec the specifications of the certificates to order
     * @return the created certificates test data
     * @throws DataException if there is an error during user creation
     */
    CertificatesData createCertificatesData(CertificatesSpec certificatesSpec) throws DataException;

    /**
     * Deletes the certificates test data for the given id.
     *
     * @param id the ID generated while creating certificates
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteCertificatesData(String id) throws DataException;

    /**
     * Deletes all appeals data for a given penalty reference and company number.
     *
     * @param companyNumber    the company number
     * @param penaltyReference the penalty reference
     * @return true if the entity was deleted, false otherwise
     * @throws DataException if there is an error during deletion
     */
    boolean deleteAppealsData(String companyNumber, String penaltyReference) throws DataException;

    /**
     * Gets the account penalty data for a given company code, customer code and penalty reference.
     *
     * @param companyCode      the company code
     * @param customerCode     the customer code
     * @param penaltyReference the penalty reference
     * @return the {@link AccountPenaltiesData} with only the requested penalty reference in the
     * {@link PenaltyData} list
     * @throws NoDataFoundException if the penalty cannot be found
     */
    AccountPenaltiesData getAccountPenaltyData(String companyCode, String customerCode,
            String penaltyReference) throws NoDataFoundException;

    /**
     * Gets the account penalties data for a given company code and customer code.
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @return @return the {@link AccountPenaltiesData}
     * @throws NoDataFoundException if the penalty cannot be found
     */
    AccountPenaltiesData getAccountPenaltiesData(String companyCode, String customerCode)
            throws NoDataFoundException;

    /**
     * Updates the account penalties data for a given penalty reference and
     * {@link UpdateAccountPenaltiesRequest}
     *
     * @param penaltyRef the penalty reference
     * @param request    the update request
     * @return the {@link AccountPenaltiesData}
     * @throws NoDataFoundException if the requested penalty reference cannot be found
     * @throws DataException if the account penalties cannot be updated
     */
    AccountPenaltiesData updateAccountPenaltiesData(String penaltyRef,
            UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException;

    /**
     * Deletes all account penalties data for a given company code and customer code
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalties cannot be found
     * @throws DataException        if the account penalties failed to be deleted
     */
    ResponseEntity<Void> deleteAccountPenaltiesData(String companyCode, String customerCode)
            throws NoDataFoundException, DataException;

    /**
     * Retrieves postcodes for a given country.
     *
     * @param country the country for which to retrieve postcodes
     * @return Postcodes object containing the postcodes for the specified country
     * @throws DataException if there is an error retrieving the postcodes
     */
    PostcodesData getPostcodes(String country) throws DataException;
}
