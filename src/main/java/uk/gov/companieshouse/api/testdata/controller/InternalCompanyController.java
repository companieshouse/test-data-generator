package uk.gov.companieshouse.api.testdata.controller;

import jakarta.validation.Valid;
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
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.DeleteCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequestV2;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyAuthCodeResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyUpdateResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.PopulatedCompanyDetailsResponse;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CreateCompanyWorkflowService;
import uk.gov.companieshouse.api.testdata.service.DeleteCompanyWorkflowService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Handles internal company endpoints for company creation, deletion, and
 * pre-populated structure operations.
 * Auth code validation is only applied on internal delete requests when an auth
 * code is provided; create endpoints do not require an auth code.
 */
@RestController
@RequestMapping(value = "${api.endpoint}/internal", produces = MediaType.APPLICATION_JSON_VALUE)
public class InternalCompanyController {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private final CreateCompanyWorkflowService createCompanyWorkflowService;
    private final DeleteCompanyWorkflowService deleteCompanyWorkflowService;
    private final CompanyAuthCodeService companyAuthCodeService;
    private final CompanyProfileService companyProfileService;

    private static final String COMPANY_NUMBER_DATA = "company number";
    private static final String JURISDICTION_DATA = "jurisdiction";
    private static final String NEW_COMPANY_CREATED = "New company created";
    private static final String API_VERSION_HEADER = "X-API-Version";
    private static final String STATUS = "status";
    private static final String ERROR = "error";

    public InternalCompanyController(
            CreateCompanyWorkflowService createCompanyWorkflowService,
            DeleteCompanyWorkflowService deleteCompanyWorkflowService,
            CompanyAuthCodeService companyAuthCodeService,
            CompanyProfileService companyProfileService) {
        this.createCompanyWorkflowService = createCompanyWorkflowService;
        this.deleteCompanyWorkflowService = deleteCompanyWorkflowService;
        this.companyAuthCodeService = companyAuthCodeService;
        this.companyProfileService = companyProfileService;
    }

    @PostMapping(value = "/company", headers = API_VERSION_HEADER + "=2")
    public ResponseEntity<CompanyProfileResponse> createInternalCompanyV2(
            @Valid @RequestBody(required = false) InternalCompanyRequestV2 internalCompanyRequestV2) throws DataException {
        InternalCompanyRequest internalCompanyRequest = internalCompanyRequestV2 == null
                ? new InternalCompanyRequest()
                : internalCompanyRequestV2.toInternalCompanyRequest();
        return createCompanyResponse(internalCompanyRequest);
    }

    @PostMapping("/company")
    public ResponseEntity<CompanyProfileResponse> createInternalCompanyV1(
            @Valid @RequestBody(required = false) InternalCompanyRequest internalCompanyRequest) throws DataException {
        return createCompanyResponse(internalCompanyRequest);
    }

    private ResponseEntity<CompanyProfileResponse> createCompanyResponse(InternalCompanyRequest request)
            throws DataException {

        Optional<InternalCompanyRequest> optionalRequest = Optional.ofNullable(request);
        InternalCompanyRequest internalCompanyRequest = optionalRequest.orElse(new InternalCompanyRequest());

        CompanyProfileResponse createdCompany = createCompanyWorkflowService.createInternalCompany(internalCompanyRequest);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        data.put(JURISDICTION_DATA, internalCompanyRequest.getJurisdiction());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @DeleteMapping({"/company/{companyNumber}"})
    public ResponseEntity<Void> deleteCompany(
            @PathVariable String companyNumber,
            @Valid @RequestBody(required = false) DeleteCompanyRequest request)
            throws DataException, NoDataFoundException, InvalidAuthCodeException {

        if (request != null && request.getAuthCode() != null
                && !companyAuthCodeService.verifyAuthCode(companyNumber, request.getAuthCode())) {
            throw new InvalidAuthCodeException(companyNumber);
        }
        deleteCompanyWorkflowService.deleteCompany(companyNumber);

        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, companyNumber);
        LOG.info("Internal Company deleted", data);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/get-populated-company-structure")
    public ResponseEntity<PopulatedCompanyDetailsResponse> buildCompanyDataStructure(
            @Valid @RequestBody(required = false) InternalCompanyRequest request) throws DataException {

        Optional<InternalCompanyRequest> optionalRequest = Optional.ofNullable(request);
        InternalCompanyRequest internalCompanyRequest = optionalRequest.orElse(new InternalCompanyRequest());

        var companyData = createCompanyWorkflowService.buildCompanyDataStructure(internalCompanyRequest);
        return new ResponseEntity<>(companyData, HttpStatus.OK);
    }

    @PostMapping("/create-company-with-populated-structure")
    public ResponseEntity<CompanyProfileResponse> persistCompanyDataStructure(
            @Valid @RequestBody(required = false) CompanyWithPopulatedStructureRequest request)
            throws DataException {
        Optional<CompanyWithPopulatedStructureRequest> optionalRequest = Optional.ofNullable(request);
        CompanyWithPopulatedStructureRequest companyWithPopulatedStructureRequest =
                optionalRequest.orElse(new CompanyWithPopulatedStructureRequest());
        var createdCompany = createCompanyWorkflowService.persistCompanyDataStructure(companyWithPopulatedStructureRequest);
        Map<String, Object> data = new HashMap<>();
        data.put(COMPANY_NUMBER_DATA, createdCompany.getCompanyNumber());
        LOG.info(NEW_COMPANY_CREATED, data);
        return new ResponseEntity<>(createdCompany, HttpStatus.CREATED);
    }

    @GetMapping("/company/authcode")
    public ResponseEntity<CompanyAuthCodeResponse> findOrCreateCompanyAuthCode(
            @RequestParam("companyNumber") final String companyNumber)
            throws DataException, NoDataFoundException {

        if (companyNumber == null || companyNumber.isEmpty()) {
            throw new DataException("companyNumber query parameter is required");
        }

        var authCode = companyAuthCodeService.findOrCreate(companyNumber);
        var defaultAuthCode = new CompanyAuthCodeResponse(authCode.getAuthCode());
        return new ResponseEntity<>(defaultAuthCode, HttpStatus.OK);
    }

    @PutMapping("/update-company")
    public ResponseEntity<Object> updateCompany(
            @Valid @RequestBody UpdateCompanyRequest request) {

        try {
            CompanyProfile updatedCompany =
                    companyProfileService.updateCompanyProfile(request);

            CompanyUpdateResponse response = new CompanyUpdateResponse(
                    updatedCompany.getCompanyNumber(),
                    "updated"
            );

            return ResponseEntity.ok(response);

        } catch (NoDataFoundException ex) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put(ERROR, ex.getMessage());
            errorBody.put(STATUS, HttpStatus.NOT_FOUND.value());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorBody);
        } catch (DataException ex) {
            Map<String, Object> errorBody = new HashMap<>();
            errorBody.put(ERROR, ex.getMessage());
            errorBody.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorBody);
        }
    }
}
