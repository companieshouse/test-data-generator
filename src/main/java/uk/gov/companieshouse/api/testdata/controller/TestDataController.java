package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
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
            response.put("status", HttpStatus.NOT_FOUND);
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
            response.put("status", HttpStatus.NOT_FOUND);
            LOG.info("Identity not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/acsp-members")
    public ResponseEntity<AcspMembersData> createAcspMember(
            @Valid @RequestBody AcspMembersSpec request) throws DataException {

        AcspMembersData createdAcspMember = testDataService.createAcspMembersData(request);

        Map<String, Object> data = new HashMap<>();
        data.put("acsp member id", createdAcspMember.getAcspMemberId());
        LOG.info("New acsp member created", data);
        return new ResponseEntity<>(createdAcspMember, HttpStatus.CREATED);
    }

    @DeleteMapping("/acsp-members/{acspMemberId}")
    public ResponseEntity<Map<String, Object>> deleteAcspMember(@PathVariable("acspMemberId")
                                                                    String acspMemberId)
            throws DataException {
        Map<String, Object> response = new HashMap<>();
        response.put("acsp member id", acspMemberId);
        boolean deleteAcspMember = testDataService.deleteAcspMembersData(acspMemberId);

        if (deleteAcspMember) {
            LOG.info("Acsp Member deleted", response);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            response.put("status", HttpStatus.NOT_FOUND);
            LOG.info("Acsp member not found", response);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }


}
