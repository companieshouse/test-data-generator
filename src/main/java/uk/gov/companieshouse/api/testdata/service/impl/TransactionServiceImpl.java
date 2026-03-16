package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.Objects;

import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspApplication;
import uk.gov.companieshouse.api.testdata.model.entity.Transactions;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.repository.AcspApplicationRepository;
import uk.gov.companieshouse.api.testdata.repository.TransactionsRepository;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.DataService;

@Service
public class TransactionServiceImpl implements DataService<TransactionsResponse, TransactionsRequest>  {

    @Autowired
    private TransactionsRepository repository;

    @Autowired
    private AcspApplicationRepository acsprepository;

    @Autowired
    private RandomService randomService;

    public TransactionsResponse create(TransactionsRequest txnSpec) throws DataException {
        var randomId = randomService.getTransactionId();
        final var txn = new Transactions();
        final var acspApplication = new AcspApplication();
        String email = txnSpec.getEmail() != null ? txnSpec.getEmail() :
                "test-data-generated" + randomId + "@chtesttdg.mailosaur.net";
        var acspApplicationId = randomService.getTransactionId();
        var foreName = "Forename-TestData";
        var surName = "Surname-TestData";
        var description = "Create an ACSP registration transaction";
        var reference = "ACSP Registration";
        var typeOfBusiness = "limited-company";
        var status ="open";
        txn.setId(randomId);
        txn.setEmail(email);
        txn.setUserId(Objects.requireNonNullElse(txnSpec.getUserId(),"12345678911"));
        txn.setForename(foreName);
        txn.setSurname(surName);
        txn.setDescription(description);
        txn.setReference(reference);
        txn.setResumeUri( "/register-as-companies-house-authorised-agent/resume?transactionId="+randomId+"&acspId="+acspApplicationId);
        txn.setStatus(status);
        acspApplication.setId(acspApplicationId);
        acspApplication.setTypeOfBusiness(typeOfBusiness);
        acspApplication.setUserId(Objects.requireNonNullElse(txnSpec.getUserId(),"12345678911"));
        acspApplication.setSelf("/transactions/"+randomId+"/authorised-corporate-service-provider-applications/"+acspApplicationId);
        repository.save(txn);
        acsprepository.save(acspApplication);
        return new TransactionsResponse(txn.getId(), txn.getEmail(), txn.getUserId(), txn.getReference(), txn.getResumeUri(), txn.getStatus(), acspApplicationId);
    }

    @Override
    public boolean delete(String transactionId) {
        var txnOpt = repository.findById(transactionId);
        if (txnOpt.isEmpty()) {
            return false;
        }

        Transactions txn = txnOpt.get();
        String resumeUri = txn.getResumeUri();
        String acspApplicationId = extractAcspId(resumeUri);

        if (acspApplicationId != null) {
            var acspOpt = acsprepository.findById(acspApplicationId);
            acspOpt.ifPresent(acsprepository::delete);
        }

        repository.delete(txn);
        return true;
    }

    private String extractAcspId(String uri) {
        if (uri == null) return null;

        var pattern = Pattern.compile("acspId=([^&]+)");
        var matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }
}
