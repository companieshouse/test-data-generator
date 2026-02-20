package uk.gov.companieshouse.api.testdata.service.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;
import uk.gov.companieshouse.api.testdata.model.rest.response.AccountPenaltiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspMembersResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspMembersRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.AdminPermissionsResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AdminPermissionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CertificatesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertificatesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CertifiedCopiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CombinedSicActivitiesResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CombinedSicActivitiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyAuthAllowListRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.enums.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyWithPopulatedStructureRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.MissingImageDeliveriesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PenaltySpec;
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
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.AdminPermissionsRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.repository.MissingImageDeliveriesRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AcspProfileService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.AppointmentService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanyPscService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.api.testdata.service.CompanyWithPopulatedStructureService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.UserService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;


@Service
public class TestDataServiceImpl implements TestDataService {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final int COMPANY_NUMBER_LENGTH = 8;

    @Autowired
    private CompanyProfileService companyProfileService;
    @Autowired
    private DataService<FilingHistory, CompanyRequest> filingHistoryService;
    @Autowired
    private CompanyAuthCodeService companyAuthCodeService;
    @Autowired
    private AppointmentService appointmentService;
    @Autowired
    private DataService<CompanyMetrics, CompanyRequest> companyMetricsService;
    @Autowired
    private CompanyPscStatementServiceImpl companyPscStatementService;
    @Autowired
    private CompanyPscService companyPscService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataService<AcspMembersResponse, AcspMembersRequest> acspMembersService;
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
    private AcspMembersRepository acspMembersRepository;
    @Autowired
    private AdminPermissionsRepository adminPermissionsRepository;
    @Autowired
    private CertificatesRepository certificatesRepository;
    @Autowired
    private CertifiedCopiesRepository certifiedCopiesRepository;
    @Autowired
    private MissingImageDeliveriesRepository missingImageDeliveriesRepository;
    @Autowired
    private DataService<TransactionsResponse, TransactionsRequest> transactionService;
    @Autowired
    private AcspProfileService acspProfileService;
    @Autowired
    private CompanyAuthAllowListService companyAuthAllowListService;
    @Autowired
    AppealsService appealsService;
    @Autowired
    private DataService<CompanyRegisters, CompanyRequest> companyRegistersService;
    @Autowired
    @Qualifier("companySearchService")
    private CompanySearchService companySearchService;
    @Autowired
    private AccountPenaltiesService accountPenaltiesService;
    @Autowired
    @Qualifier("alphabeticalCompanySearchService")
    private CompanySearchService alphabeticalCompanySearch;
    @Autowired
    @Qualifier("advancedCompanySearchService")
    private CompanySearchService advancedCompanySearch;
    @Autowired
    private PostcodeService postcodeService;
    @Autowired
    private DataService<Disqualifications, CompanyRequest> disqualificationsService;
    @Autowired
    private DataService<UserCompanyAssociationResponse,
            UserCompanyAssociationRequest> userCompanyAssociationService;
    @Autowired
    private DataService<AdminPermissionsResponse, AdminPermissionsRequest> adminPermissionsService;

    private final CompanyWithPopulatedStructureService companyWithPopulatedStructureService;

    @Value("${api.url}")
    private String apiUrl;

    @Value("${elastic.search.deployed}")
    private boolean isElasticSearchDeployed;

    void setAPIUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    void setElasticSearchDeployed(Boolean isElasticSearchDeployed) {
        this.isElasticSearchDeployed = isElasticSearchDeployed;
    }

    public TestDataServiceImpl(
            CompanyWithPopulatedStructureService companyWithPopulatedStructureService) {
        this.companyWithPopulatedStructureService = companyWithPopulatedStructureService;
    }

    @Override
    public CompanyProfileResponse createCompanyData(final CompanyRequest spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanyRequest can not be null");
        }

        String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);

        if (spec.getIsPaddingCompanyNumber() != null) {
            companyNumberPrefix = companyNumberPrefix + "000";
        }

        do {
            spec.setCompanyNumber(companyNumberPrefix
                    + randomService
                    .getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));

        spec.setCompanyWithPopulatedStructureOnly(false);

        try {
            companyProfileService.create(spec);
            LOG.info("Successfully created company profile");

            filingHistoryService.create(spec);
            LOG.info("Successfully created filing history");

            if (spec.getNoDefaultOfficer() == null || !spec.getNoDefaultOfficer()) {
                appointmentService.createAppointment(spec);
                LOG.info("Successfully created appointments ");
            }

            var authCode = companyAuthCodeService.create(spec);
            LOG.info("Successfully created auth code: " + authCode.getAuthCode());

            companyMetricsService.create(spec);
            LOG.info("Successfully created company metrics");

            companyPscStatementService.createPscStatements(spec);
            LOG.info("Successfully created all PSC statements based on spec counts.");

            companyPscService.create(spec);
            LOG.info("Successfully created PSCs");
            if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
                LOG.info("Creating company registers for company: " + spec.getCompanyNumber());
                this.companyRegistersService.create(spec);
                LOG.info("Successfully created company registers");
            }
            if (spec.getDisqualifiedOfficers()
                    != null && !spec.getDisqualifiedOfficers().isEmpty()) {
                disqualificationsService.create(spec);
                LOG.info("Successfully created disqualifications");
            }

            String companyUri = this.apiUrl + "/company/" + spec.getCompanyNumber();
            var companyData = new CompanyProfileResponse(spec.getCompanyNumber(),
                    authCode.getAuthCode(), companyUri);
            addCompanyToElasticSearchIndexes(spec, companyData);
            LOG.info("Successfully created all company data for: " + spec.getCompanyNumber());
            return companyData;
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", spec.getCompanyNumber());
            data.put("error message", ex.getMessage());
            LOG.error("Failed to create company data for company number: "
                    + spec.getCompanyNumber(), ex, data);
            // Rollback all successful insertions
            deleteCompanyData(spec.getCompanyNumber());
            throw new DataException("Failed to create company data in service", ex);
        }
    }

    @Override
    public void deleteCompanyData(String companyId) throws DataException {
        LOG.info("Deleting company data for company number: " + companyId);
        List<Exception> suppressedExceptions = new ArrayList<>();

        deleteUkEstablishmentsIfOverseaCompany(companyId, suppressedExceptions);

        deleteSingleCompanyData(companyId, suppressedExceptions);

        if (!suppressedExceptions.isEmpty()) {
            LOG.error("Errors occurred while deleting company data for company number: "
                    + companyId);
            var errorMessage = new StringBuilder(
                    "Error deleting company data. Details: ");
            for (var i = 0; i < suppressedExceptions.size(); i++) {
                var ex = suppressedExceptions.get(i);
                errorMessage.append(" [").append(i + 1).append("] ")
                        .append(ex.getMessage() != null ? ex.getMessage() : "Unknown error");
                if (ex.getCause() != null) {
                    errorMessage.append(" Cause: ").append(ex.getCause().getMessage()
                            != null ? ex.getCause().getMessage() : "Unknown cause");
                }
            }
            var ex = new DataException(errorMessage.toString());
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }
    }

    private void deleteUkEstablishmentsIfOverseaCompany(
            String companyId, List<Exception> suppressedExceptions) {
        try {
            LOG.info("Checking if company number " + companyId + " is an oversea company.");
            Optional<CompanyProfile> companyProfile =
                    companyProfileService.getCompanyProfile(companyId);
            if (isOverseaCompany(companyProfile)) {
                LOG.info("Company number " + companyId
                        + " is an oversea company. Deleting UK establishments.");
                deleteUkEstablishmentsForParent(companyId, suppressedExceptions);
            } else {
                LOG.info("Company number " + companyId
                        + " is not an oversea company. Skipping UK establishments deletion.");
            }
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
    }

    private boolean isOverseaCompany(Optional<CompanyProfile> companyProfile) {
        boolean isOversea = companyProfile.isPresent()
                && CompanyType.OVERSEA_COMPANY.getValue().equals(companyProfile.get().getType());
        LOG.debug("Is company oversea: " + isOversea);
        return isOversea;
    }

    private void deleteUkEstablishmentsForParent(String companyId,
                                                 List<Exception> suppressedExceptions) {
        LOG.info("Fetching UK establishments for parent company number: " + companyId);
        List<String> ukEstablishments =
                companyProfileService.findUkEstablishmentsByParent(companyId);
        if (ukEstablishments.isEmpty()) {
            LOG.info("No UK establishments found for company number: " + companyId);
        } else {
            for (String ukEstablishmentNumber : ukEstablishments) {
                try {
                    LOG.info("Deleting UK establishment with company number: "
                            + ukEstablishmentNumber);
                    deleteSingleCompanyData(ukEstablishmentNumber, suppressedExceptions);
                } catch (Exception de) {
                    suppressedExceptions.add(de);
                }
            }
        }
    }

    private void deleteSingleCompanyData(String companyId, List<Exception> suppressedExceptions) {
        LOG.info("Deleting single company data for company number: " + companyId);
        try {
            this.companyProfileService.delete(companyId);
            LOG.info("Deleted company profile for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.filingHistoryService.delete(companyId);
            LOG.info("Deleted filing history for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyAuthCodeService.delete(companyId);
            LOG.info("Deleted company auth code for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.appointmentService.deleteAllAppointments(companyId);
            LOG.info("Deleted appointments for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscStatementService.delete(companyId);
            LOG.info("Deleted PSC statements for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscService.delete(companyId);
            LOG.info("Deleted PSCs for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyMetricsService.delete(companyId);
            LOG.info("Deleted company metrics for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyAuthAllowListService.delete(companyId);
            LOG.info("Deleted company auth allow list for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyRegistersService.delete(companyId);
            LOG.info("Deleted company registers for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.disqualificationsService.delete(companyId);
            LOG.info("Deleted disqualifications for company number: " + companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        if (isElasticSearchDeployed) {
            try {
                LOG.info("Attempting to delete "
                        + "company from ElasticSearch index for company number: " + companyId);
                this.companySearchService.deleteCompanyFromElasticSearchIndex(companyId);
                this.alphabeticalCompanySearch.deleteCompanyFromElasticSearchIndex(companyId);
                this.advancedCompanySearch.deleteCompanyFromElasticSearchIndex(companyId);
                LOG.info("Deleted company from ElasticSearch index for company number: "
                        + companyId);
            } catch (Exception ex) {
                LOG.error("Failed to delete company from ElasticSearch index for company number: "
                        + companyId, ex);
            }
        }
    }

    @Override
    public CompanyAuthCode findOrCreateCompanyAuthCode(String companyNumber)
            throws DataException, NoDataFoundException {
        try {
            return companyAuthCodeService.findOrCreate(companyNumber);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException(
                    "Company profile not found when finding or creating auth code");
        } catch (Exception ex) {
            throw new DataException("Error finding or creating company auth code", ex);
        }
    }

    @Override
    public UserResponse createUserData(UserRequest userRequest) throws DataException {
        final String password = userRequest.getPassword();
        if (password == null || password.isEmpty()) {
            throw new DataException("Password is required to create a user");
        }

        List<String> adminPermissionIds = userRequest.getRoles();
        if (adminPermissionIds != null && !adminPermissionIds.isEmpty()) {
            List<String> permissionStrings = new ArrayList<>();

            for (String groupName : adminPermissionIds) {
                var adminPermissionEntity = adminPermissionsRepository
                        .findByGroupName(groupName);

                if (adminPermissionEntity != null
                        && adminPermissionEntity.getPermissions() != null) {
                    permissionStrings.addAll(adminPermissionEntity.getPermissions());
                }
            }
            if (!permissionStrings.isEmpty()) {
                userRequest.setRoles(permissionStrings);
            }
        }

        var userData = userService.create(userRequest);
        if (userRequest.getIsCompanyAuthAllowList() != null && userRequest.getIsCompanyAuthAllowList()) {
            var companyAuthAllowListSpec = new CompanyAuthAllowListRequest();
            companyAuthAllowListSpec.setEmailAddress(userData.getEmail());
            companyAuthAllowListService.create(companyAuthAllowListSpec);
        }
        return userData;
    }

    @Override
    public boolean deleteUserData(String userId) {
        var user = userService.getUserById(userId).orElse(null);
        if (user == null) {
            return false;
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            var allowListId = companyAuthAllowListService.getAuthId(user.getEmail());
            if (allowListId != null) {
                companyAuthAllowListService.delete(allowListId);
            }
        }
        return this.userService.delete(userId);
    }


    @Override
    public AcspMembersResponse createAcspMembersData(final AcspMembersRequest spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create an ACSP member");
        }

        var acspProfileSpec = new AcspProfileRequest();
        if (spec.getAcspProfile() != null) {
            acspProfileSpec = spec.getAcspProfile();
        }

        try {
            var acspProfileData = createAcspProfile(acspProfileSpec);
            spec.setAcspNumber(acspProfileData.getAcspNumber());

            AcspMembersResponse createdMember = createAcspMember(spec);

            return new AcspMembersResponse(
                    new ObjectId(createdMember.getAcspMemberId()),
                    createdMember.getAcspNumber(),
                    createdMember.getUserId(),
                    createdMember.getStatus(),
                    createdMember.getUserRole()
            );
        } catch (Exception ex) {
            throw new DataException(ex);
        }
    }

    private AcspProfileResponse createAcspProfile(AcspProfileRequest acspProfileRequest)
            throws DataException {
        try {
            return this.acspProfileService.create(acspProfileRequest);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP profile", ex);
        }
    }

    private AcspMembersResponse createAcspMember(AcspMembersRequest spec) throws DataException {
        try {
            return this.acspMembersService.create(spec);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP member", ex);
        }
    }

    @Override
    public boolean deleteAcspMembersData(String acspMemberId) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();
        try {
            var maybeMember = acspMembersRepository.findById(acspMemberId);
            if (maybeMember.isPresent()) {
                var member = maybeMember.get();
                String acspNumber = member.getAcspNumber();

                deleteAcspMember(acspMemberId, suppressedExceptions);
                deleteAcspProfile(acspNumber, suppressedExceptions);
            } else {
                return false;
            }
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        if (!suppressedExceptions.isEmpty()) {
            var ex = new DataException("Error deleting acsp member's data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }
        return true;
    }

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
    public AccountPenaltiesResponse createPenaltyData(PenaltySpec penaltySpec) throws DataException {
        try {
            LOG.info("Creating account penalties for company code: " + penaltySpec.getCompanyCode()
                    + " and customer code: " + penaltySpec.getCustomerCode());
            return accountPenaltiesService.createAccountPenalties(penaltySpec);
        } catch (Exception ex) {
            LOG.error("Failed to create account penalties for company code: "
                    + penaltySpec.getCompanyCode()
                    + " and customer code: " + penaltySpec.getCustomerCode(), ex);
            throw new DataException("Error creating account penalties", ex);
        }
    }

    @Override
    public PostcodesResponse getPostcodes(String country) throws DataException {
        try {
            List<Postcodes> postcodes = postcodeService.getPostcodeByCountry(country);
            if (postcodes == null || postcodes.isEmpty()) {
                LOG.info("No postcodes found for country: " + country);
                return null;
            }
            var secureRandom = new SecureRandom();
            var randomPostcode = secureRandom.nextInt(postcodes.size());
            return getPostCodesData(postcodes).get(randomPostcode);
        } catch (Exception ex) {
            throw new DataException("Error retrieving postcodes", ex);
        }
    }

    private static List<PostcodesResponse> getPostCodesData(List<Postcodes> postcodes) {
        List<PostcodesResponse> postcodesResponseList = new ArrayList<>();
        for (Postcodes postcode : postcodes) {
            var postcodeData = new PostcodesResponse(
                    postcode.getBuildingNumber() != null ? postcode
                            .getBuildingNumber().intValue() : null,
                    postcode.getThoroughfare().getName() + " "
                            + (postcode.getThoroughfare().getDescriptor()
                            != null ? postcode.getThoroughfare().getDescriptor() : ""),
                    postcode.getLocality().getDependentLocality(),
                    postcode.getLocality().getPostTown(),
                    postcode.getPostcode().getPretty()
            );
            postcodesResponseList.add(postcodeData);
        }
        return postcodesResponseList;
    }

    public Optional<AcspProfile> getAcspProfileData(String acspNumber)
            throws NoDataFoundException {

        return acspProfileService.getAcspProfile(acspNumber);
    }

    private void deleteAcspMember(String acspMemberId, List<Exception> suppressedExceptions) {
        try {
            acspMembersService.delete(acspMemberId);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP member", ex));
        }
    }

    private void deleteAcspProfile(String acspNumber, List<Exception> suppressedExceptions) {
        try {
            this.acspProfileService.delete(acspNumber);
        } catch (Exception ex) {
            suppressedExceptions.add(new DataException("Error deleting ACSP profile", ex));
        }
    }

    @Override
    public UserCompanyAssociationResponse createUserCompanyAssociationData(
            UserCompanyAssociationRequest spec)
            throws DataException {
        if (spec.getUserId() == null
                && spec.getUserEmail() == null) {
            throw new DataException("A user_id or a user_email is "
                    + "required to create an association");
        }

        if (spec.getCompanyNumber() == null || spec.getCompanyNumber().isEmpty()) {
            throw new DataException("Company number is "
                    + "required to create an association");
        }

        try {
            UserCompanyAssociationResponse createdAssociation =
                    userCompanyAssociationService.create(spec);

            return new UserCompanyAssociationResponse(
                    new ObjectId(createdAssociation.getId()),
                    createdAssociation.getCompanyNumber(),
                    createdAssociation.getUserId(),
                    createdAssociation.getUserEmail(),
                    createdAssociation.getStatus(),
                    createdAssociation.getApprovalRoute(),
                    createdAssociation.getInvitations()
            );
        } catch (Exception ex) {
            throw new DataException("Error creating the association",
                    ex);
        }
    }

    @Override
    public boolean deleteUserCompanyAssociationData(String id) throws DataException {
        try {
            return userCompanyAssociationService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting association", ex);
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
    public AdminPermissionsResponse createAdminPermissionsData(
            AdminPermissionsRequest spec) throws DataException {
        return adminPermissionsService.create(spec);
    }

    @Override
    public boolean deleteAdminPermissionsData(String id) throws DataException {
        try {
            return adminPermissionsService.delete(id);
        } catch (Exception ex) {
            throw new DataException("Error deleting admin permissions", ex);
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
    public CompanyProfileResponse createPublicCompanyData(PublicCompanyRequest publicCompanySpec)
            throws DataException {
        var companySpec = new CompanyRequest();

        // Only set allowed fields from PublicCompanyRequest
        companySpec.setJurisdiction(publicCompanySpec.getJurisdiction());
        companySpec.setCompanyType(publicCompanySpec.getCompanyType());
        companySpec.setCompanyStatus(publicCompanySpec.getCompanyStatus());
        companySpec.setSubType(publicCompanySpec.getSubType());
        companySpec.setHasSuperSecurePscs(publicCompanySpec.getHasSuperSecurePscs());
        companySpec.setNumberOfAppointments(publicCompanySpec.getNumberOfAppointments());
        companySpec.setSecureOfficer(publicCompanySpec.getSecureOfficer());
        companySpec.setRegisters(publicCompanySpec.getRegisters());
        companySpec.setCompanyStatusDetail(publicCompanySpec.getCompanyStatusDetail());
        companySpec.setFilingHistoryList(publicCompanySpec.getFilingHistoryList());
        if (!CollectionUtils.isEmpty(publicCompanySpec.getFilingHistoryList())) {
            companySpec.getFilingHistoryList().forEach(filing -> filing.setDocumentMetadata(false));
        }
        companySpec.setNumberOfAppointments(publicCompanySpec.getNumberOfAppointments());
        companySpec.setOfficerRoles(publicCompanySpec.getOfficerRoles());
        companySpec.setAccountsDueStatus(publicCompanySpec.getAccountsDueStatus());
        companySpec.setNumberOfPscs(publicCompanySpec.getNumberOfPscs());
        companySpec.setPscType(publicCompanySpec.getPscType());
        companySpec.setPscActive(publicCompanySpec.getPscActive());
        companySpec.setWithdrawnStatements(publicCompanySpec.getWithdrawnStatements());
        companySpec.setActiveStatements(publicCompanySpec.getActiveStatements());
        companySpec.setHasUkEstablishment(publicCompanySpec.getHasUkEstablishment());
        companySpec.setRegisteredOfficeIsInDispute(
                publicCompanySpec.getRegisteredOfficeIsInDispute());
        companySpec.setUndeliverableRegisteredOfficeAddress(
                publicCompanySpec.getUndeliverableRegisteredOfficeAddress());
        if (publicCompanySpec.getForeignCompanyLegalForm() != null
                && !publicCompanySpec.getForeignCompanyLegalForm().isBlank()) {
            companySpec.setForeignCompanyLegalForm(publicCompanySpec.getForeignCompanyLegalForm());
        }
        return createCompanyData(companySpec);
    }

    private void addCompanyToElasticSearchIndexes(CompanyRequest spec,
                                                  CompanyProfileResponse companyData)
            throws DataException, ApiErrorResponseException, URIValidationException {

        // This variable is set from environment to allow disabling ES indexing in certain environments
        if (!isElasticSearchDeployed) {
            LOG.debug("Elasticsearch not deployed; skipping indexing for company " + spec.getCompanyNumber());
            return;
        }

        // Decide which indexes to update
        boolean addCompanyIndex =
                Boolean.TRUE.equals(spec.getAddToCompanyElasticSearchIndex())
                        || spec.getAlphabeticalSearch() != null
                        || spec.getAdvancedSearch() != null;

        boolean addAlphabeticalIndex = spec.getAlphabeticalSearch() != null;
        boolean addAdvancedIndex = spec.getAdvancedSearch() != null;

        // Company index (ensure present if any specialised index is requested)
        if (addCompanyIndex) {
            LOG.info("Adding company to ElasticSearch index: " + spec.getCompanyNumber());
            companySearchService.addCompanyIntoElasticSearchIndex(companyData);
        }

        // Alphabetical index
        if (addAlphabeticalIndex) {
            LOG.info("Adding company to Alphabetical ElasticSearch index: " + spec.getCompanyNumber());
            alphabeticalCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
        }

        // Advanced index
        if (addAdvancedIndex) {
            LOG.info("Adding company to Advanced ElasticSearch index: " + spec.getCompanyNumber());
            advancedCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
        }

        LOG.info("Successfully added company to configured ElasticSearch indexes : " + spec.getCompanyNumber());
    }

    @Override
    public PopulatedCompanyDetailsResponse getCompanyDataStructureBeforeSavingInMongoDb(CompanyRequest spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanyRequest can not be null");
        }
        String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);
        if (spec.getIsPaddingCompanyNumber() != null) {
            companyNumberPrefix = companyNumberPrefix + "000";
        }
        do {
            spec.setCompanyNumber(companyNumberPrefix
                    + randomService
                    .getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));

        try {
            var response = new PopulatedCompanyDetailsResponse();
            spec.setCompanyWithPopulatedStructureOnly(true);

            var companyProfile = companyProfileService.create(spec);
            LOG.info("Successfully get company profile");
            response.setCompanyProfile(companyProfile);

            var filingHistory = filingHistoryService.create(spec);
            LOG.info("Successfully get filing history");
            response.setFilingHistory(filingHistory);

            if (spec.getNoDefaultOfficer() == null || !spec.getNoDefaultOfficer()) {
                var appointments = appointmentService.createAppointment(spec);
                LOG.info("Successfully get appointments ");
                response.setAppointmentsData(appointments);
            }

            var authCode = companyAuthCodeService.create(spec);
            LOG.info("Successfully get auth code: " + authCode.getAuthCode());
            response.setCompanyAuthCode(authCode);


            var companyMetrics = companyMetricsService.create(spec);
            LOG.info("Successfully get company metrics");
            response.setCompanyMetrics(companyMetrics);

            List<CompanyPscStatement> companyPscStatements = companyPscStatementService.createPscStatements(spec);
            LOG.info("Successfully get all PSC statements based on spec counts.");
            response.setCompanyPscStatement(companyPscStatements);

            var companyPscs = companyPscService.create(spec);
            LOG.info("Successfully get PSCs");
            response.setCompanyPscs(companyPscs);

            if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
                var companyRegisters = this.companyRegistersService.create(spec);
                LOG.info("Successfully get company registers");
                response.setCompanyRegisters(companyRegisters);
            }

            if (spec.getDisqualifiedOfficers()
                    != null && !spec.getDisqualifiedOfficers().isEmpty()) {
                var disqualifications = disqualificationsService.create(spec);
                LOG.info("Successfully get disqualifications");
                response.setDisqualifications(disqualifications);
            }

            return response;
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", spec.getCompanyNumber());
            data.put("error message", ex.getMessage());
            LOG.error("Failed to get company data for company number: "
                    + spec.getCompanyNumber(), ex, data);
            throw new DataException("Failed to get company data from service", ex);
        }
    }

    @Override
    public CompanyProfileResponse createCompanyWithStructure(CompanyWithPopulatedStructureRequest companySpec) throws DataException {

        var companyNumber = companySpec.getCompanyProfile().getCompanyNumber();
        var authCode = companySpec.getCompanyAuthCode().getAuthCode();
        companyWithPopulatedStructureService.createCompanyWithPopulatedStructure(companySpec);
        String companyUri = this.apiUrl + "/company/" + companyNumber;
        return new CompanyProfileResponse(companyNumber,
                authCode, companyUri);
    }

}
