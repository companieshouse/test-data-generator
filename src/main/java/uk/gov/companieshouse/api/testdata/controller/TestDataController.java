package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyDeleteRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.service.FilingHistoryService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestDataController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    private final TestDataService testDataService;

    private final VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;
    private final FilingHistoryService filingHistoryService;

    public TestDataController(
            TestDataService testDataService,
            VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService,
            FilingHistoryService filingHistoryService) {
        this.testDataService = testDataService;
        this.verifiedIdentityService = verifiedIdentityService;
        this.filingHistoryService = filingHistoryService;
    }

    @PostMapping("/internal/certificates")
    public ResponseEntity<CertificatesResponse> createCertificates(
            @Valid @RequestBody CertificatesRequest request) throws DataException {

        var createdCertificates = testDataService.createCertificatesData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("certificated-id", createdCertificates.getCertificates().getFirst().getId());
        LOG.info("New certificates added", data);
        return new ResponseEntity<>(createdCertificates, HttpStatus.CREATED);
    }

    @PostMapping("/internal/certified-copies")
    public ResponseEntity<CertificatesResponse> createCertifiedCopies(
            @Valid @RequestBody CertifiedCopiesRequest request) throws DataException {

        var createdCertifiedCopies = testDataService.createCertifiedCopiesData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("certificates-copies-id",
                createdCertifiedCopies.getCertificates().getFirst().getId());
        LOG.info("New certified copies added", data);
        return new ResponseEntity<>(createdCertifiedCopies, HttpStatus.CREATED);
    }

    @PostMapping("/internal/missing-image-deliveries")
    public ResponseEntity<CertificatesResponse> createMissingImageDeliveries(
            @Valid @RequestBody MissingImageDeliveriesRequest request) throws DataException {

        var createdMissingImageDeliveries =
                testDataService.createMissingImageDeliveriesData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("missing-image-deliveries-id",
                createdMissingImageDeliveries.getCertificates().getFirst().getId());
        LOG.info("New missing image deliveries added", data);
        return new ResponseEntity<>(createdMissingImageDeliveries, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/certificates/{id}")
    public ResponseEntity<Map<String, Object>> deleteCertificates(@PathVariable("id")
                                                                  String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        boolean deleteCertificates = testDataService.deleteCertificatesData(id);

        if (deleteCertificates) {
            LOG.info("Certificate is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Certificate Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/internal/certified-copies/{id}")
    public ResponseEntity<Map<String, Object>> deleteCertifiedCopies(@PathVariable("id")
                                                                     String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        boolean deleteCertifiedCopies = testDataService.deleteCertifiedCopiesData(id);

        if (deleteCertifiedCopies) {
            LOG.info("Certified Copies is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Certified Copies Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/internal/missing-image-deliveries/{id}")
    public ResponseEntity<Map<String, Object>> deleteMissingImageDeliveries(@PathVariable("id")
                                                                            String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("id", id);
        boolean deleteMissingImageDeliveries = testDataService.deleteMissingImageDeliveriesData(id);

        if (deleteMissingImageDeliveries) {
            LOG.info("Missing Image Deliveries is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Missing Image Deliveries Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/internal/appeals")
    public ResponseEntity<Void> deleteAppeal(
            @Valid @RequestBody DeleteAppealsRequest request) throws DataException {

        if (request == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean isDeleted = testDataService
                .deleteAppealsData(request.getCompanyNumber(), request.getPenaltyReference());

        if (isDeleted) {
            LOG.info("Appeals data deleted for company number: " + request.getCompanyNumber()
                    + " and penalty reference: " + request.getPenaltyReference());
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            LOG.info("No appeals data found for company number: " + request.getCompanyNumber()
                    + " and penalty reference: " + request.getPenaltyReference());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/internal/penalties")
    public ResponseEntity<Object> createPenalty(
            @Valid @RequestBody PenaltyRequest request) throws DataException {
        LOG.info("Creating new account penalties for company code: " + request.getCompanyCode()
                + " and customer code: " + request.getCustomerCode());

        var createdPenalties = testDataService.createPenaltyData(request);

        if (Boolean.TRUE.equals(request.isDuplicate())
                && (createdPenalties == null || createdPenalties.getPenalties().isEmpty())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put(ERROR, "number_of_penalties "
                    + "should be greater than 1 for duplicate penalties");
            errorResponse.put(STATUS, HttpStatus.BAD_REQUEST.value());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        LOG.info("Successfully created account penalties with ID: "
                + createdPenalties.getIdAsString());
        return new ResponseEntity<>(createdPenalties, HttpStatus.CREATED);
    }

    @GetMapping("/internal/penalties/{id}")
    public ResponseEntity<AccountPenaltiesResponse> getAccountPenalties(
            @PathVariable("id") String id,
            @RequestParam(name = "transactionReference", required = false)
            String transactionReference) throws NoDataFoundException {
        var accountPenaltiesData = testDataService.getAccountPenaltiesData(id);
        if (transactionReference != null) {
            accountPenaltiesData.setPenalties(
                    accountPenaltiesData.getPenalties().stream()
                            .filter(penalty -> transactionReference.equals(
                                    penalty.getTransactionReference()))
                            .toList()
            );
        }
        return ResponseEntity.ok(accountPenaltiesData);
    }

    @GetMapping("/internal/penalties/query")
    public ResponseEntity<AccountPenaltiesResponse> getAccountPenaltiesByCustomerCodeAndCompanyCode(
            @RequestParam(name = "customerCode") String customerCode,
            @RequestParam(name = "companyCode") String companyCode) throws NoDataFoundException {

        var accountPenaltiesData = testDataService.getAccountPenaltiesData(
                customerCode, companyCode);

        return ResponseEntity.ok(accountPenaltiesData);
    }

    @PutMapping("/internal/penalties/{penaltyRef}")
    public ResponseEntity<AccountPenaltiesResponse> updateAccountPenalties(
            @PathVariable("penaltyRef") String penaltyRef,
            @Valid @RequestBody UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {

        var accountPenaltiesData = testDataService.updateAccountPenaltiesData(
                penaltyRef, request);

        return new ResponseEntity<>(accountPenaltiesData, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/penalties/{id}")
    public ResponseEntity<Void> deleteAccountPenalties(
            @PathVariable("id") String id,
            @RequestBody(required = false) PenaltyDeleteRequest request)
            throws DataException, NoDataFoundException {

        if (request == null || request.getTransactionReference() == null) {
            return testDataService.deleteAccountPenaltiesData(id);
        } else {
            return testDataService.deleteAccountPenaltyByReference(
                    id, request.getTransactionReference());
        }
    }

    @PostMapping("/internal/transactions")
    public ResponseEntity<TransactionsResponse> createTransaction(
            @Valid @RequestBody TransactionsRequest request) throws DataException {

        Optional<TransactionsRequest> optionalRequest = Optional.ofNullable(request);
        TransactionsRequest spec = optionalRequest.orElse(new TransactionsRequest());

        TransactionsResponse createdTransaction = testDataService.createTransactionData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("_id", createdTransaction.getId());
        data.put("reference", spec.getReference());
        LOG.info("Transaction created", data);
        return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/transactions/{transactionId}")
    public ResponseEntity<Map<String, Object>> deleteTransaction(
            @PathVariable("transactionId") String transactionId) throws DataException {

        Map<String, Object> response = new HashMap<>();
        response.put("transaction_id", transactionId);
        boolean deleteTransaction = testDataService.deleteTransaction(transactionId);

        if (deleteTransaction) {
            LOG.info("Transaction is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Transaction is not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/internal/combined-sic-activities")
    public ResponseEntity<CombinedSicActivitiesResponse> createCombinedSicActivities(
            @Valid @RequestBody CombinedSicActivitiesRequest request) throws DataException {

        var createdSicCodeKeyword = testDataService.createCombinedSicActivitiesData(request);

        Map<String, Object> data = Map.of("sic-code-keyword-id", createdSicCodeKeyword.getId());
        LOG.info("New sic code keyword added", new HashMap<>(data));
        return new ResponseEntity<>(createdSicCodeKeyword, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/combined-sic-activities/{id}")
    public ResponseEntity<Map<String, Object>> deleteCombinedSicActivities(
            @PathVariable("id") String id) throws DataException {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("id", id);
        boolean deleteCombinedSicActivities = testDataService.deleteCombinedSicActivitiesData(id);

        if (deleteCombinedSicActivities) {
            LOG.info("Combined Sic Activities is deleted", logMap);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            logMap.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Combined Sic Activities Not Found", logMap);
            return new ResponseEntity<>(logMap, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/internal/identity/verification")
    public ResponseEntity<IdentityVerificationResponse> getIdentityVerification(
            @RequestParam("email") String email)
            throws DataException, NoDataFoundException {

        var data = verifiedIdentityService.getIdentityVerificationData(email);
        if (data == null) {
            throw new NoDataFoundException("No identity verification found for email: " + email);
        }

        return new ResponseEntity<>(data, HttpStatus.OK);
    }

    @DeleteMapping("/internal/item-groups/{orderNumber}")
    public ResponseEntity<Map<String, Object>> deleteItemGroups(@PathVariable("orderNumber")
    String orderNumber)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("orderNumber", orderNumber);
        boolean deleteItemGroups = testDataService.deleteItemGroupsData(orderNumber);

        if (deleteItemGroups) {
            LOG.info("Item Groups is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Item Groups Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/internal/company-filing-history")
    public ResponseEntity<Object> getCompanyFilingHistory(
            @RequestParam(value = "companyNumber", required = false) String companyNumber,
            @RequestParam(value = "id", required = false) String companyFilingHistoryId) {

        if ((companyNumber == null || companyNumber.isEmpty()) &&
                (companyFilingHistoryId == null || companyFilingHistoryId.isEmpty())) {

            Map<String, Object> error = new HashMap<>();
            error.put(ERROR, "Either companyNumber or id must be provided");
            error.put(STATUS, HttpStatus.BAD_REQUEST.value());
            return ResponseEntity.badRequest().body(error);
        }

        if (companyNumber != null && !companyNumber.isEmpty()) {

            List<FilingHistory> results =
                    filingHistoryService.getCompanyFilingHistoryByCompanyNumber(companyNumber);

            if (results.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(results);

        } else {

            Optional<FilingHistory> result =
                    filingHistoryService.getCompanyFilingHistoryById(companyFilingHistoryId);

            if (result.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(result.get());
        }

    }

    @DeleteMapping("/internal/company-filing-history/{companyNumber}")
    public ResponseEntity<Object> deleteCompanyFilingHistory(
            @PathVariable String companyNumber) throws NoDataFoundException {

        boolean deleted = filingHistoryService.deleteCompanyFilingHistory(companyNumber);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("companyNumber", companyNumber);
            response.put(STATUS, HttpStatus.NOT_FOUND.value());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
