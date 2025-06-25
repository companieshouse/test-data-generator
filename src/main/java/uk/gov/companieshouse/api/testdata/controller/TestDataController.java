package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteAppealsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsData;
import uk.gov.companieshouse.api.testdata.model.rest.DisqualificationsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.PostcodesData;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
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

    @PostMapping("/company")
    public ResponseEntity<CompanyData> createCompany(
            @Valid @RequestBody(required = false) CompanySpec request) throws DataException {

        Optional<CompanySpec> optionalRequest = Optional.ofNullable(request);
        CompanySpec spec = optionalRequest.orElse(new CompanySpec());

        CompanyData createdCompany = testDataService.createCompanyData(spec);

        Map<String, Object> data = new HashMap<>();
        data.put("company number", createdCompany.getCompanyNumber());
        data.put("jurisdiction", spec.getJurisdiction());
        LOG.info("New company created", data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @DeleteMapping("/company/{companyNumber}")
    public ResponseEntity<Void> deleteCompany(
            @PathVariable("companyNumber") String companyNumber,
            @Valid @RequestBody DeleteCompanyRequest request)
            throws DataException, InvalidAuthCodeException, NoDataFoundException {

        if (!companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())) {
            throw new InvalidAuthCodeException(companyNumber);
        }

        testDataService.deleteCompanyData(companyNumber);

        Map<String, Object> data = new HashMap<>();
        data.put("company number", companyNumber);
        LOG.info("Company deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/user")
    public ResponseEntity<UserData> createUser(@Valid @RequestBody() UserSpec request)
            throws DataException {
        var createdUser = testDataService.createUserData(request);
        Map<String, Object> data = new HashMap<>();
        data.put("user email", createdUser.getEmail());
        data.put("user id", createdUser.getId());
        LOG.info("New user created", data);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @DeleteMapping("/user/{userId}")
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

    @PostMapping("/identity")
    public ResponseEntity<Map<String, Object>> createIdentity(
            @Valid @RequestBody() IdentitySpec request) throws DataException {
        var createdIdentity = testDataService.createIdentityData(request);
        Map<String, Object> data = new HashMap<>();
        data.put("identity id", createdIdentity.getId());
        LOG.info("New identity created", data);
        return new ResponseEntity<>(data, HttpStatus.CREATED);
    }

    @DeleteMapping("/identity/{identityId}")
    public ResponseEntity<Map<String, Object>> deleteIdentity(
            @PathVariable("identityId") String identityId) throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("identity id", identityId);
        boolean deleteIdentity = testDataService.deleteIdentityData(identityId);

        if (deleteIdentity) {
            LOG.info("Identity deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Identity not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/acsp-members")
    public ResponseEntity<AcspMembersData> createAcspMember(
            @Valid @RequestBody AcspMembersSpec request) throws DataException {

        var createdAcspMember = testDataService.createAcspMembersData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp-member-id", createdAcspMember.getAcspMemberId());
        LOG.info("New acsp member created", data);
        return new ResponseEntity<>(createdAcspMember, HttpStatus.CREATED);
    }

    @PostMapping("/certificates")
    public ResponseEntity<CertificatesData> createCertificates(
            @Valid @RequestBody CertificatesSpec request) throws DataException {

        var createdCertificates = testDataService.createCertificatesData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("certificated-id", createdCertificates.getId());
        LOG.info("New certificates added", data);
        return new ResponseEntity<>(createdCertificates, HttpStatus.CREATED);
    }

    @DeleteMapping("/acsp-members/{acspMemberId}")
    public ResponseEntity<Map<String, Object>> deleteAcspMember(
            @PathVariable("acspMemberId") String acspMemberId)
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

    @DeleteMapping("/certificates/{id}")
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

    @DeleteMapping("/appeals")
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

    @GetMapping("/penalties/{penaltyRef}")
    public ResponseEntity<AccountPenaltiesData> getPenalty(
            @NotNull @PathVariable("penaltyRef") String penaltyRef,
            @Valid @RequestBody AccountPenaltyRequest request) throws NoDataFoundException {

        AccountPenaltiesData penaltyData = testDataService.getAccountPenaltyData(
                request.getCompanyCode(), request.getCustomerCode(), penaltyRef);

        return new ResponseEntity<>(penaltyData, HttpStatus.OK);

    }

    @GetMapping("/penalties")
    public ResponseEntity<AccountPenaltiesData> getAccountPenalties(
            @Valid @RequestBody AccountPenaltyRequest request) throws NoDataFoundException {

        var accountPenaltiesData = testDataService.getAccountPenaltiesData(
                request.getCompanyCode(), request.getCustomerCode());

        return new ResponseEntity<>(accountPenaltiesData, HttpStatus.OK);

    }

    @PutMapping("/penalties/{penaltyRef}")
    public ResponseEntity<AccountPenaltiesData> updateAccountPenalties(
            @PathVariable("penaltyRef") String penaltyRef,
            @Valid @RequestBody UpdateAccountPenaltiesRequest request)
            throws NoDataFoundException, DataException {

        var accountPenaltiesData = testDataService.updateAccountPenaltiesData(
                penaltyRef, request);

        return new ResponseEntity<>(accountPenaltiesData, HttpStatus.CREATED);

    }

    @DeleteMapping("/penalties")
    public ResponseEntity<Void> deleteAccountPenalties(
            @Valid @RequestBody AccountPenaltyRequest request)
            throws DataException, NoDataFoundException {
        return testDataService.deleteAccountPenaltiesData(
                request.getCompanyCode(), request.getCustomerCode());
    }

    @GetMapping("/postcodes")
    public ResponseEntity<PostcodesData> getPostcode(
            @RequestParam(value = "country") String country) throws DataException {
        LOG.info("Retrieving postcode for country: " + country);
        var postcode = testDataService.getPostcodes(country);
        if (postcode == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LOG.info("Retrieved postcode for country: " + country + " " + postcode.getPostcode());
        return new ResponseEntity<>(postcode, HttpStatus.OK);
    }

    @PostMapping("/disqualified-officers")
    public ResponseEntity<DisqualificationsData> createDisqualification(
            @Valid @RequestBody DisqualificationsSpec request) throws DataException {

        var createdDisqualification = testDataService.createDisqualificationsData(request);

        String officerTypePath = request.getIsCorporateOfficer() ? "corporate" : "natural";
        String disqualificationsUri = String.format("/disqualified-officers/%s/%s",
                officerTypePath,
                createdDisqualification.getId()
        );
        var response = new DisqualificationsData(createdDisqualification.getId(),
                createdDisqualification.getDateOfBirth(), disqualificationsUri);

        Map<String, Object> data = new HashMap<>();
        data.put("company_number", request.getCompanyNumber());
        data.put("disqualification_id", createdDisqualification.getId());
        data.put("officer_type", officerTypePath);
        LOG.info("New disqualification created for " + officerTypePath + "officer " + data);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/disqualified-officers/{id}")
    public ResponseEntity<Map<String, Object>> deleteDisqualification(
            @PathVariable("id") String id) throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("disqualification_id", id);
        boolean deleteDisqualification = testDataService.deleteDisqualificationsData(id);

        if (deleteDisqualification) {
            LOG.info("Disqualification deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put(STATUS, HttpStatus.NOT_FOUND);
            LOG.info("Disqualification not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }
}
