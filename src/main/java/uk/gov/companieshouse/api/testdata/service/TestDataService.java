package uk.gov.companieshouse.api.testdata.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;


public interface TestDataService {

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
     * Adds a new transaction and acsp application test data based on the provided specifications.
     *
     * @param transactionsRequest the specifications of the transactions
     * @return the transactions and acsp application id
     * @throws DataException if there is an error during transactions or acsp application creation
     */
    TransactionsResponse createTransactionData(TransactionsRequest transactionsRequest) throws DataException;

    boolean deleteTransaction(String transactionId) throws DataException;

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
     * Deletes the Item Groups test data for the given order number.
     *
     * @param orderNumber the order number generated while creating Item Groups
     * @throws DataException if there is an error during user deletion
     */
    boolean deleteItemGroupsData(String orderNumber) throws DataException;
}
