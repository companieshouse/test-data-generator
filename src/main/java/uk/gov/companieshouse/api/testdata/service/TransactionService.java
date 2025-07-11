package uk.gov.companieshouse.api.testdata.service;


import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.exception.DataException;


public interface TransactionService {

   /**
     * Create Transaction with given {@code transactionSpec}.
     *
     * @param transactionSpec The specification the new transaction must adhere to
     * @return A {@link TransactionsData}
     * @throws DataException If any error occurs
     */
    TransactionsData create(TransactionsSpec transactionSpec) throws DataException;

}