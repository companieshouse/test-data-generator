package uk.gov.companieshouse.api.testdata.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.Transactions;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
 

    @Autowired
    private TransactionsRepository repository;

    @Autowired
    private RandomService randomService;

 
    public TransactionsData create(TransactionsSpec txnSpec) throws DataException {
        var randomId = randomService.getTransactionId();
        final var txn = new Transactions();
        String email = txnSpec.getEmail() != null ? txnSpec.getEmail() :
                "test-data-generated" + randomId + "@chtesttdg.mailosaur.net";

        txn.setId(randomId);
        txn.setEmail(email);
        txn.setForename("Forename-TestData");
        txn.setSurname("Surname-TestData");
        txn.setDescription("Create an ACSP registration transaction");
        txn.setReference("ACSP Registration");
        repository.save(txn);
        return new TransactionsData(txn.getId(), txn.getEmail(), txn.getForename(), txn.getSurname(),txn.getUserId(),txn.getDescription(),txn.getReference());
    }

}
