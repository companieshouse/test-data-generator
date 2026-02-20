package uk.gov.companieshouse.api.testdata.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PenaltyResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;

public interface AccountPenaltiesService {

    /**
     * Gets an account penalties entity by its company code, customer code and penalty reference.
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @param penaltyRef   the penalty reference
     * @return the {@link AccountPenaltiesResponse} with only the penalty reference that has been
     requested in the {@link PenaltyResponse} list
     * @throws NoDataFoundException if the account penalty cannot be found
     */
    AccountPenaltiesResponse getAccountPenalty(String companyCode, String customerCode,
                                               String penaltyRef) throws NoDataFoundException;

    /**
     * Gets an account penalties entity by its ID.
     *
     * @param id  the ID of the AccountPenalties Mongo DB collection
     * @return the {@link AccountPenaltiesResponse} with all penalties the {@link PenaltyResponse} list
     * @throws NoDataFoundException if the account penalties cannot be found
     */
    AccountPenaltiesResponse getAccountPenalties(String id)
            throws NoDataFoundException;

    /**
     * Gets an account penalties entity by its company code and customer code.
     *
     * @param customerCode the customer code
     * @param companyCode the company code
     * @return the {@link AccountPenaltiesResponse} with all penalties the {@link PenaltyResponse} list
     * @throws NoDataFoundException if the account penalties cannot be found
     */
    AccountPenaltiesResponse getAccountPenalties(String customerCode, String companyCode)
            throws NoDataFoundException;

    /**
     * Updates an account penalties entity for the requested penalty reference.
     *
     * @param penaltyRef the penalty reference.
     * @param request    the update request.
     * @return the {@link AccountPenaltiesResponse} with the updated penalty in the {@link PenaltyResponse}
     * list
     * @throws NoDataFoundException if the account penalty cannot be found
     * @throws DataException if the account penalty cannot be updated
     */
    AccountPenaltiesResponse updateAccountPenalties(String penaltyRef,
                                                    UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException;

    /** .
     * Deletes an account penalties entity by its company code and customer code
     *
     * @param id the penalty Id
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalties cannot be found
     */
    ResponseEntity<Void> deleteAccountPenalties(String id)
            throws NoDataFoundException;

    /**
     * Deletes an account penalty by its reference.
     *
     * @param id            the company code
     * @param penaltyRef    the penalty reference
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalty cannot be found
     * @throws DataException if the account penalty failed to be deleted
     */
    ResponseEntity<Void> deleteAccountPenaltyByReference(String id, String penaltyRef)
            throws NoDataFoundException;

    AccountPenaltiesResponse createAccountPenalties(PenaltyRequest penaltyRequest) throws DataException;
}
