package uk.gov.companieshouse.api.testdata.service;

import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltyData;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;

public interface AccountPenaltiesService {

    /**
     * Gets an account penalties entity by its company code, customer code and penalty reference.
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @param penaltyRef   the penalty reference
     * @return the {@link AccountPenaltiesData} with only the penalty reference that has been
     * requested in the {@link PenaltyData} list
     * @throws NoDataFoundException if the account penalty cannot be found
     */
    AccountPenaltiesData getAccountPenalty(String companyCode, String customerCode,
            String penaltyRef) throws NoDataFoundException;

    /**
     * Gets an account penalties entity by its company code and customer code.
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @return the {@link AccountPenaltiesData} with all penalties the {@link PenaltyData} list
     * @throws NoDataFoundException if the account penalties cannot be found
     */
    AccountPenaltiesData getAccountPenalties(String companyCode, String customerCode)
            throws NoDataFoundException;

    /**
     * Updates an account penalties entity for the requested penalty reference.
     *
     * @param penaltyRef the penalty reference
     * @param request    the update request
     * @return the {@link AccountPenaltiesData} with the updated penalty in the {@link PenaltyData}
     * list
     * @throws NoDataFoundException if the account penalty cannot be found
     * @throws DataException if the account penalty cannot be updated
     */
    AccountPenaltiesData updateAccountPenalties(String penaltyRef,
            UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException;

    /**
     * Deletes an account penalties entity by its company code and customer code
     *
     * @param companyCode  the company code
     * @param customerCode the customer code
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the account penalties cannot be found
     */
    ResponseEntity<Void> deleteAccountPenalties(String companyCode, String customerCode)
            throws NoDataFoundException;
}
