package uk.gov.companieshouse.api.testdata.service;


import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.exception.DataException;


public interface TransactionService {

   /**
     * Create Transaction with given {@code transactionSpec}.
     *
     * @param transactionSpec The specification the new transaction must adhere to
     * @return A {@link TransactionsResponse}
     * @throws DataException If any error occurs
     */
    TransactionsResponse create(TransactionsRequest transactionSpec) throws DataException;

}