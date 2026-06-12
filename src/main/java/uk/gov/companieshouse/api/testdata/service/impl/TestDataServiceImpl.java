package uk.gov.companieshouse.api.testdata.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.ItemGroupsService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


@Service
public class TestDataServiceImpl implements TestDataService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private DataService<CertificatesResponse, CertificatesRequest> certificatesService;
    @Autowired
    private DataService<CertificatesResponse, CertifiedCopiesRequest> certifiedCopiesService;
    @Autowired
    private DataService<CombinedSicActivitiesResponse,
            CombinedSicActivitiesRequest> combinedSicActivitiesService;
    @Autowired
    private DataService<CertificatesResponse, MissingImageDeliveriesRequest> missingImageDeliveriesService;
    @Autowired
    private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Autowired
    AppealsService appealsService;
    @Autowired
    private AccountPenaltiesService accountPenaltiesService;
    @Autowired
    private PostcodeService postcodeService;
    @Autowired
    private ItemGroupsService itemGroupsService;

    @Override
    public CertificatesResponse createCertificatesData(
            final CertificatesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create certificates");
        }

        try {
            return certificatesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating certificates", ex);
        }
    }

    @Override
    public CertificatesResponse createCertifiedCopiesData(
            final CertifiedCopiesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create certified copies");
        }
        try {
            return certifiedCopiesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating certified copies", ex);
        }
    }

    @Override
    public CertificatesResponse createMissingImageDeliveriesData(
            final MissingImageDeliveriesRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create missing image deliveries");
        }

        try {
            return missingImageDeliveriesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating missing image deliveries", ex);
        }
    }

    @Override
    public CombinedSicActivitiesResponse createCombinedSicActivitiesData(
            final CombinedSicActivitiesRequest spec) throws DataException {
        try {
            return combinedSicActivitiesService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating Sic code and keyword", ex);
        }
    }


    @Override
    public boolean deleteCertificatesData(String id) throws DataException {
        try {
            return certificatesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting certificates", ex);
        }
    }

    @Override
    public boolean deleteCertifiedCopiesData(String id) throws DataException {
        try {
            return certifiedCopiesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting certified copies", ex);
        }
    }

    @Override
    public boolean deleteMissingImageDeliveriesData(String id) throws DataException {
        try {
            return missingImageDeliveriesService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting missing image deliveries", ex);
        }
    }

    @Override
    public boolean deleteAppealsData(String companyNumber, String penaltyReference)
            throws DataException {
        try {
            return appealsService.delete(companyNumber, penaltyReference);
        } catch (Exception ex) {
            throw new DataException("Error deleting appeals data", ex);
        }
    }

    @Override
    public boolean deleteCombinedSicActivitiesData(String id)
            throws DataException {
        try {
            return combinedSicActivitiesService.delete(String.valueOf(id));
        } catch (Exception ex) {
            throw new DataException("Error deleting appeals data", ex);
        }
    }

    @Override
    public AccountPenaltiesResponse getAccountPenaltiesData(String id)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(id);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesResponse getAccountPenaltiesData(String customerCode, String companyCode)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(customerCode, companyCode);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesResponse updateAccountPenaltiesData(
            String penaltyRef, UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.updateAccountPenalties(penaltyRef, request);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error updating account penalties - not found");
        } catch (Exception ex) {
            throw new DataException("Error updating account penalties", ex);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltiesData(String id)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.deleteAccountPenalties(id);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error deleting account penalties - not found");
        } catch (Exception ex) {
            throw new DataException("Error deleting account penalties", ex);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltyByReference(
            String id, String transactionReference)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.deleteAccountPenaltyByReference(
                    id, transactionReference);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error deleting account penalty - not found");
        } catch (Exception ex) {
            throw new DataException("Error deleting account penalty", ex);
        }
    }

    @Override
    public AccountPenaltiesResponse createPenaltyData(PenaltyRequest penaltyRequest) throws DataException {
        try {
            LOG.info("Creating account penalties for company code: " + penaltyRequest.getCompanyCode()
                    + " and customer code: " + penaltyRequest.getCustomerCode());
            return accountPenaltiesService.createAccountPenalties(penaltyRequest);
        } catch (Exception ex) {
            LOG.error("Failed to create account penalties for company code: "
                    + penaltyRequest.getCompanyCode()
                    + " and customer code: " + penaltyRequest.getCustomerCode(), ex);
            throw new DataException("Error creating account penalties", ex);
        }
    }

    public TransactionsResponse createTransactionData(TransactionsRequest transactionsRequest)
            throws DataException {
        try {
            LOG.info("Creating Txn for User Id: " + transactionsRequest.getUserId());
            return transactionService.create(transactionsRequest);
        } catch (Exception ex) {
            LOG.error("Failed to create Transaction for User Id: "
                    + transactionsRequest.getUserId());
            throw new DataException("Error creating transaction", ex);
        }
    }

    @Override
    public boolean deleteTransaction(String transactionId) throws DataException {
        try {
            return transactionService.delete(transactionId);
        } catch (Exception ex) {
            throw new DataException("Error deleting transaction", ex);
        }
    }

    @Override
    public boolean deleteItemGroupsData(String orderNumber) throws DataException {
        try {
            return itemGroupsService.deleteItemGroups(orderNumber);
        } catch (Exception ex) {
            throw new DataException("Error deleting Item Groups", ex);
        }
    }
}
