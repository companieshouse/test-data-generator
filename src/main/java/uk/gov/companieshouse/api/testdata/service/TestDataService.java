package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;


public interface TestDataService {

    /**
     * Create company data with given {@code companySpec}.
     *
     * @param companySpec The specification the new company must adhere to
     * @return A {@link CompanyProfileResponse}
     * @throws DataException If any error occurs
     */
    CompanyProfileResponse createCompanyData(CompanyRequest companySpec) throws DataException;

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
     * @param userRequest the specifications of the user to create
     * @return the created user's test data
     * @throws DataException if there is an error during user creation
     */
    UserResponse createUserData(UserRequest userRequest) throws DataException;

    /**
     * Deletes a user test data by their user ID.
     *
     * @param userId the ID of the user to delete
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteUserData(String userId) throws DataException;

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
    CertificatesResponse createCertificatesData(CertificatesRequest certificatesRequest) throws DataException;

    /**
     * Adds a new certified copies test data based on the provided user specifications.
     *
     * @param certifiedCopiesRequest the specifications of the certified copies to order
     * @return the created certificates test data
     * @throws DataException if there is an error during user creation
     */
    CertificatesResponse createCertifiedCopiesData(
            CertifiedCopiesRequest certifiedCopiesRequest) throws DataException;

    /**
     * Adds a new missing image deliveries test data based on the provided user specifications.
     *
     * @param missingImageDeliveriesRequest the specifications of the missing image deliveries to order
     * @return the created certificates test data
     * @throws DataException if there is an error during user creation
     */
    CertificatesResponse createMissingImageDeliveriesData(
            MissingImageDeliveriesRequest missingImageDeliveriesRequest) throws DataException;

    /**
     * Adds a new sic code test data based on the provided user specifications.
     *
     * @param combinedSicActivitiesRequest the specifications of the sic code
     * @return the created sic code with keyword test data
     * @throws DataException if there is an error during user creation
     */
    CombinedSicActivitiesResponse createCombinedSicActivitiesData(
            CombinedSicActivitiesRequest combinedSicActivitiesRequest)  throws DataException;

    /**
     * Deletes the certificates test data for the given id.
     *
     * @param id the ID generated while creating certificates
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteCertificatesData(String id) throws DataException;

    /**
     * Deletes the certified copies test data for the given id.
     *
     * @param id the ID generated while creating certificates
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteCertifiedCopiesData(String id) throws DataException;

    /**
     * Deletes the missing image deliveries test data for the given id.
     *
     * @param id the ID generated while creating certificates
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteMissingImageDeliveriesData(String id) throws DataException;

    /**
     * Deletes all appeals data for a given penalty reference and company number.
     *
     * @param companyNumber    the company number
     * @param penaltyReference the penalty reference
     * @return true if the entity was deleted, false otherwise
     * @throws DataException if there is an error during deletion
     */
    boolean deleteAppealsData(String companyNumber, String penaltyReference) throws DataException;

    boolean deleteCombinedSicActivitiesData(String id)
        throws DataException;

    /**
     * Gets the account penalties data for a given company by AccountPenalties ID.
     *
     * @param id  the ID of the AccountPenalties Mongo DB collection
     * @return @return the {@link AccountPenaltiesResponse}
     * @throws NoDataFoundException if the penalty cannot be found
     */
    AccountPenaltiesResponse getAccountPenaltiesData(String id)
            throws NoDataFoundException;

    /**
     * Gets the account penalties data for a given company code and customer code.
     *
     * @param customerCode  the customer code
     * @param companyCode  the company code
     * @return @return the {@link AccountPenaltiesResponse}
     * @throws NoDataFoundException if the penalty cannot be found
     */
    AccountPenaltiesResponse getAccountPenaltiesData(String customerCode, String companyCode)
            throws NoDataFoundException;

    /**.
     * Updates the account penalties data for a given penalty reference and
     * {@link UpdateAccountPenaltiesRequest}
     *
     * @param penaltyRef the penalty reference
     * @param request    the update request
     * @return the {@link AccountPenaltiesResponse}
     * @throws NoDataFoundException if the requested penalty reference cannot be found
     * @throws DataException if the account penalties cannot be updated
     */
    AccountPenaltiesResponse updateAccountPenaltiesData(String penaltyRef,
                                                        UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException;

    /**.
     * Deletes all account penalties data for a given company code and customer code
     *
     * @param id  the penalty id
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalties cannot be found
     * @throws DataException        if the account penalties failed to be deleted
     */
    ResponseEntity<Void> deleteAccountPenaltiesData(String id)
            throws NoDataFoundException, DataException;

    /**
     * Deletes an account penalty by its reference.
     *
     * @param id                 the company code
     * @param transactionReference the transaction reference
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalty cannot be found
     * @throws DataException        if the account penalty failed to be deleted
     */
    ResponseEntity<Void> deleteAccountPenaltyByReference(String id, String transactionReference)
            throws NoDataFoundException, DataException;

    AccountPenaltiesResponse createPenaltyData(PenaltyRequest penaltyRequest) throws DataException;

    /**
     * Retrieves postcodes for a given country.
     *
     * @param country the country for which to retrieve postcodes
     * @return Postcodes object containing the postcodes for the specified country
     * @throws DataException if there is an error retrieving the postcodes
     */
    PostcodesResponse getPostcodes(String country) throws DataException;

    /**
     * Gets the ACSP profile data for a given ACSP number.
     *
     * @param acspNumber the ACSP number
     * @return the {@link AcspProfileResponse}
     * @throws NoDataFoundException if the profile cannot be found
     */
    Optional<AcspProfile> getAcspProfileData(String acspNumber)
            throws NoDataFoundException;

    /**
     * Adds a new transaction and acsp application test data based on the provided specifications.
     *
     * @param transactionsRequest the specifications of the transactions
     * @return the transactions and acsp application id
     * @throws DataException if there is an error during transactions or acsp application creation
     */
    TransactionsResponse createTransactionData(TransactionsRequest transactionsRequest) throws DataException;

    boolean deleteTransaction(String transactionId) throws DataException;

    /**
     * Creates a new user company association test data based on the
     * provided user specifications.
     *
     * @param userCompanyAssociationRequest the specifications of the
     *                                   association to create
     * @return the created user company association test data
     * @throws DataException if there is an error during creation
     */
    UserCompanyAssociationResponse
            createUserCompanyAssociationData(UserCompanyAssociationRequest userCompanyAssociationRequest)
            throws DataException;

    /**
     * Deletes a user company association test data by
     * association id.
     *
     * @param associationId the ID of the association to delete
     * @throws DataException if there is an error during deletion
     */
    boolean deleteUserCompanyAssociationData(String associationId) throws DataException;

    AdminPermissionsResponse createAdminPermissionsData(AdminPermissionsRequest spec) throws DataException;

    /**
     * Deletes admin permissions data by its ID.
     *
     * @param id the ID of the admin permissions to delete
     * @return true if the admin permissions were deleted, false otherwise
     * @throws DataException if there is an error during deletion
     */
    boolean deleteAdminPermissionsData(String id) throws DataException;

    /**
     * Create public company data with given {@code companySpec}.
     *
     * @param companySpec The specification the new public company must adhere to
     * @return A {@link CompanyProfileResponse}
     * @throws DataException If any error occurs
     */
    CompanyProfileResponse createPublicCompanyData(PublicCompanyRequest companySpec) throws DataException;

    /**
     * Find an existing CompanyAuthCode for the given company number
     * or create a default one if none exists.
     * @param companyNumber the company number
     * @return the existing or newly created CompanyAuthCode
     * @throws DataException on general errors
     * @throws NoDataFoundException if the company profile is not found
     */
    CompanyAuthCode findOrCreateCompanyAuthCode(String companyNumber)
            throws DataException, NoDataFoundException;

    /**
     * Get the company data structure before saving in MongoDB.
     *
     * @param spec The specification the new company must adhere to
     * @return A {@link PopulatedCompanyDetailsResponse}
     * @throws DataException If any error occurs
     */
    PopulatedCompanyDetailsResponse getCompanyDataStructureBeforeSavingInMongoDb(CompanyRequest spec)
            throws DataException;

    /**
     * Create company with full structure based on the given {@code companySpec}.
     *
     * @param companySpec The specification the new company must adhere to
     * @return A {@link CompanyProfileResponse}
     * @throws DataException If any error occurs
     */
    CompanyProfileResponse createCompanyWithStructure(CompanyWithPopulatedStructureRequest companySpec) throws DataException;
}
