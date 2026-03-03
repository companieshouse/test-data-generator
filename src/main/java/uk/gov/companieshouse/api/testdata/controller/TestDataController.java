package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyDeleteRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyAuthCodeResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.IdentityVerificationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.TransactionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.TransactionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserCompanyAssociationResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserCompanyAssociationRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.UserResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.UserRequest;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.VerifiedIdentityService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@RestController
@RequestMapping(value = "${api.endpoint}", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestDataController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String STATUS = "status";

    @Autowired
    private TestDataService testDataService;

    @Autowired
    private CompanyAuthCodeService companyAuthCodeService;

    @Autowired
    private AccountPenaltiesService accountPenaltyService;

    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";

    @Autowired
    private VerifiedIdentityService<IdentityVerificationResponse> verifiedIdentityService;

    /* Public endpoint to create company data */
    @PostMapping("/company")
    public ResponseEntity<CompanyProfileResponse> createCompany(
            @Valid @RequestBody(required = false) PublicCompanyRequest request) throws DataException {

        Optional<PublicCompanyRequest> optionalRequest = Optional.ofNullable(request);
        PublicCompanyRequest spec = optionalRequest.orElse(new PublicCompanyRequest());

        var createdCompany = testDataService.createPublicCompanyData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, spec.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    /* Internal endpoint to create company data */
    @PostMapping("/internal/company")
    public ResponseEntity<CompanyProfileResponse> createCompanyInternal(
            @Valid @RequestBody(required = false) CompanyRequest request) throws DataException {

        Optional<CompanyRequest> optionalRequest = Optional.ofNullable(request);
        CompanyRequest spec = optionalRequest.orElse(new CompanyRequest());

        CompanyProfileResponse createdCompany = testDataService.createCompanyData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, spec.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @DeleteMapping({"/internal/company/{companyNumber}", "/company/{companyNumber}"})
    public ResponseEntity<Void> deleteCompany(
            @PathVariable("companyNumber") String companyNumber,
            @Valid @RequestBody DeleteCompanyRequest request)
            throws DataException, InvalidAuthCodeException, NoDataFoundException {

        if (!companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())) {
            throw new InvalidAuthCodeException(companyNumber);
        }

        testDataService.deleteCompanyData(companyNumber);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, companyNumber);
        LOG.info("Company deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("internal/company/authcode")
    public ResponseEntity<CompanyAuthCodeResponse> findOrCreateCompanyAuthCode(
            @RequestParam("companyNumber") final String companyNumber)
            throws DataException, NoDataFoundException {

        if (companyNumber == null || companyNumber.isEmpty()) {
            throw new DataException("companyNumber query parameter is required");
        }

        var authCode = testDataService.findOrCreateCompanyAuthCode(companyNumber);
        var defaultAuthCode = new CompanyAuthCodeResponse(authCode.getAuthCode());
        return new ResponseEntity<>(defaultAuthCode, HttpStatus.OK);
    }

    @PostMapping("/internal/user")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody() UserRequest request)
            throws DataException {
        var createdUser = testDataService.createUserData(request);
        Map<String, Object> data = new HashMap<>();
        data.put("user email", createdUser.getEmail());
        data.put("user id", createdUser.getId());
        LOG.info("New user created", data);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/internal/admin-permissions")
    public ResponseEntity<AdminPermissionsResponse> createAdminPermissions(
            @Valid @RequestBody AdminPermissionsRequest request) throws DataException {

        var createdAdminPermissions = testDataService.createAdminPermissionsData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("admin-permissions-id", createdAdminPermissions.getId());
        data.put("group-name", createdAdminPermissions.getGroupName());
        LOG.info("New admin permissions created", data);
        return new ResponseEntity<>(createdAdminPermissions, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/admin-permissions/{id}")
    public ResponseEntity<Map<String, Object>> deleteAdminPermissions(@PathVariable("id") String id)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("admin-permissions-id", id);
        boolean deleted = testDataService.deleteAdminPermissionsData(id);

        if (deleted) {
            LOG.info("Admin permissions deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Admin permissions not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/internal/user/{userId}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("userId") String userId)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("user id", userId);
        boolean deleteUser = testDataService.deleteUserData(userId);

        if (deleteUser) {
            LOG.info("User deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("User not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/internal/acsp-members")
    public ResponseEntity<AcspMembersResponse> createAcspMember(
            @Valid @RequestBody AcspMembersRequest request) throws DataException {

        var createdAcspMember = testDataService.createAcspMembersData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp-member-id", createdAcspMember.getAcspMemberId());
        LOG.info("New acsp member created", data);
        return new ResponseEntity<>(createdAcspMember, HttpStatus.CREATED);
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

    @DeleteMapping("/internal/acsp-members/{acspMemberId}")
    public ResponseEntity<Map<String, Object>> deleteAcspMember(@PathVariable("acspMemberId")
                                                                String acspMemberId)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("acsp-member-id", acspMemberId);
        boolean deleteAcspMember = testDataService.deleteAcspMembersData(acspMemberId);

        if (deleteAcspMember) {
            LOG.info("Acsp Member Deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Acsp Member Not Found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
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
            errorResponse.put("error", "number_of_penalties "
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
                            .collect(Collectors.toList())
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

    @GetMapping("/internal/postcodes")
    public ResponseEntity<PostcodesResponse> getPostcode(
            @RequestParam(value = "country") String country) throws DataException {
        LOG.info("Retrieving postcode for country: " + country);
        var postcode = testDataService.getPostcodes(country);
        if (postcode == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOG.info("Retrieved postcode for country: " + country + " " + postcode.getPostcode());
        return new ResponseEntity<>(postcode, HttpStatus.OK);
    }

    @GetMapping("/internal/acsp-profile/{acspNumber}")
    public ResponseEntity<Optional<AcspProfile>> getAcspProfile(
            @PathVariable String acspNumber) throws NoDataFoundException {

        Optional<AcspProfile> acspProfile =
                testDataService.getAcspProfileData(acspNumber);

        return new ResponseEntity<>(acspProfile, HttpStatus.OK);
    }

    @PostMapping("/internal/associations")
    public ResponseEntity<UserCompanyAssociationResponse> createAssociation(
            @Valid @RequestBody UserCompanyAssociationRequest request) throws DataException {
        var createdAssociation =
                testDataService.createUserCompanyAssociationData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("association_id",
                createdAssociation.getId());
        LOG.info("New association created", data);
        return new ResponseEntity<>(createdAssociation, HttpStatus.CREATED);
    }

    @DeleteMapping("/internal/associations/{associationId}")
    public ResponseEntity<Map<String, Object>> deleteAssociation(@PathVariable("associationId")
                                                                 String associationId)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("association_id", associationId);
        boolean deleteAssociation =
                testDataService.deleteUserCompanyAssociationData(associationId);

        if (deleteAssociation) {
            LOG.info("Association is deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Association is not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/health-check")
    public ResponseEntity<String> healthCheck() {
        LOG.info("Health check passed");
        return new ResponseEntity<>("test-data-generator is alive", HttpStatus.OK);
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

    @GetMapping("/internal/get-populated-company-structure")
    public ResponseEntity<PopulatedCompanyDetailsResponse> getCompanyWithPopulatedStructure(
            @Valid @RequestBody(required = false) CompanyRequest request) throws DataException {

        Optional<CompanyRequest> optionalRequest = Optional.ofNullable(request);
        CompanyRequest spec = optionalRequest.orElse(new CompanyRequest());

        var companyData = testDataService.getCompanyDataStructureBeforeSavingInMongoDb(spec);
        return new ResponseEntity<>(companyData, HttpStatus.OK);
    }

    @PostMapping("/internal/create-company-with-populated-structure")
    public ResponseEntity<CompanyProfileResponse> createCompanyWithPopulatedStructure(
            @Valid @RequestBody(required = false) CompanyWithPopulatedStructureRequest request) throws DataException {
        Optional<CompanyWithPopulatedStructureRequest> optionalRequest = Optional.ofNullable(request);
        CompanyWithPopulatedStructureRequest spec = optionalRequest.orElse(new CompanyWithPopulatedStructureRequest());
        var createdCompany = testDataService.createCompanyWithStructure(spec);
        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

}
