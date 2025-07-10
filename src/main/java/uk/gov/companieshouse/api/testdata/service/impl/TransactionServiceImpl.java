package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspApplication;
import uk.gov.companieshouse.api.testdata.model.entity.Transactions;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspApplicationRepository;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TransactionService;

@Service
public class TransactionServiceImpl implements TransactionService {
 

    @Autowired
    private TransactionsRepository repository;

    @Autowired
    private AcspApplicationRepository acsprepository;

    @Autowired
    private RandomService randomService;

 
    public TransactionsData create(TransactionsSpec txnSpec) throws DataException {
        var randomId = randomService.getTransactionId();
        final var txn = new Transactions();
        final var acspApplication = new AcspApplication();
        String email = txnSpec.getEmail() != null ? txnSpec.getEmail() :
                "test-data-generated" + randomId + "@chtesttdg.mailosaur.net";
        var acspApplicationId = randomService.getTransactionId();

        txn.setId(randomId);
        txn.setEmail(email);
        txn.setUserId(Objects.requireNonNullElse(txnSpec.getUserId(),"12345678911"));
        txn.setForename("Forename-TestData");
        txn.setSurname("Surname-TestData");
        txn.setDescription("Create an ACSP registration transaction");
        txn.setReference("ACSP Registration");
        txn.setResume_uri( "/register-as-companies-house-authorised-agent/resume?transactionId="+randomId+"&acspId="+acspApplicationId);
        txn.setStatus("open");
        acspApplication.setId(acspApplicationId);
        acspApplication.setTypeOfBusiness("limited-company");
        acspApplication.setUser_id(Objects.requireNonNullElse(txnSpec.getUserId(),"12345678911"));
        acspApplication.setSelf("/transactions/"+randomId+"/authorised-corporate-service-provider-applications/"+acspApplicationId);
        repository.save(txn);
        acsprepository.save(acspApplication);
        return new TransactionsData(txn.getId(), txn.getEmail(), txn.getForename(), txn.getSurname(),txn.getUserId(),txn.getDescription(),txn.getReference(),txn.getResume_uri(),txn.getStatus());
    }

}
