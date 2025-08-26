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
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;

import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;

import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertifiedCopiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesData;
import uk.gov.companieshouse.api.testdata.model.rest.CombinedSicActivitiesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyType;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.MissingImageDeliveriesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.PenaltySpec;
import uk.gov.companieshouse.api.testdata.model.rest.PostcodesData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsData;
import uk.gov.companieshouse.api.testdata.model.rest.TransactionsSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationData;
import uk.gov.companieshouse.api.testdata.model.rest.UserCompanyAssociationSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;

import uk.gov.companieshouse.api.testdata.repository.CertifiedCopiesRepository;
import uk.gov.companieshouse.api.testdata.repository.MissingImageDeliveriesRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.api.testdata.service.DataService;
import uk.gov.companieshouse.api.testdata.service.PostcodeService;
import uk.gov.companieshouse.api.testdata.service.RandomService;
import uk.gov.companieshouse.api.testdata.service.TestDataService;
import uk.gov.companieshouse.api.testdata.service.TransactionService;
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
    private DataService<FilingHistory, CompanySpec> filingHistoryService;
    @Autowired
    private CompanyAuthCodeService companyAuthCodeService;
    @Autowired
    private DataService<List<Appointment>, CompanySpec> appointmentService;
    @Autowired
    private DataService<CompanyMetrics, CompanySpec> companyMetricsService;
    @Autowired
    private CompanyPscStatementServiceImpl companyPscStatementService;
    @Autowired
    private DataService<CompanyPscs, CompanySpec> companyPscsService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private DataService<AcspMembersData, AcspMembersSpec> acspMembersService;
    @Autowired
    private DataService<CertificatesData, CertificatesSpec> certificatesService;
    @Autowired
    private DataService<CertificatesData, CertifiedCopiesSpec> certifiedCopiesService;
    @Autowired
    private DataService<CombinedSicActivitiesData, CombinedSicActivitiesSpec> combinedSicActivitiesService;
    @Autowired
    private DataService<CertificatesData, MissingImageDeliveriesSpec> missingImageDeliveriesService;
    @Autowired
    private AcspMembersRepository acspMembersRepository;
    @Autowired
    private CertificatesRepository certificatesRepository;
    @Autowired
    private CertifiedCopiesRepository certifiedCopiesRepository;
    @Autowired
    private MissingImageDeliveriesRepository missingImageDeliveriesRepository;
    @Autowired
    private DataService<RoleData, RoleSpec> roleService;
    @Autowired
    private DataService<IdentityData, IdentitySpec> identityService;
    @Autowired
    private DataService<AcspProfileData, AcspProfileSpec> acspProfileService;
    @Autowired
    private CompanyAuthAllowListService companyAuthAllowListService;
    @Autowired
    AppealsService appealsService;
    @Autowired
    private DataService<CompanyRegisters, CompanySpec> companyRegistersService;
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
    private DataService<Disqualifications, CompanySpec> disqualificationsService;

    @Autowired
    private DataService<UserCompanyAssociationData,
            UserCompanyAssociationSpec> userCompanyAssociationService;

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

    @Override
    public CompanyData createCompanyData(final CompanySpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanySpec can not be null");
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
            companyProfileService.create(spec);
            LOG.info("Successfully created company profile");

            filingHistoryService.create(spec);
            LOG.info("Successfully created filing history");

            appointmentService.create(spec);
            LOG.info("Successfully created appointments ");

            var authCode = companyAuthCodeService.create(spec);
            LOG.info("Successfully created auth code: " + authCode.getAuthCode());

            companyMetricsService.create(spec);
            LOG.info("Successfully created company metrics");

            companyPscStatementService.createPscStatements(spec);
            LOG.info("Successfully created all PSC statements based on spec counts.");

            companyPscsService.create(spec);
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
            var companyData = new CompanyData(spec.getCompanyNumber(),
                    authCode.getAuthCode(), companyUri);
            if (isElasticSearchDeployed) {
                LOG.info("Adding company to ElasticSearch index: " + spec.getCompanyNumber());
                this.companySearchService.addCompanyIntoElasticSearchIndex(companyData);
                if (spec.getAlphabeticalSearch() != null) {
                    this.alphabeticalCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
                }
                if (spec.getAdvancedSearch() != null) {
                    this.advancedCompanySearch.addCompanyIntoElasticSearchIndex(companyData);
                }
                LOG.info("Successfully added company to ElasticSearch index");
            }

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
            this.appointmentService.delete(companyId);
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
            this.companyPscsService.delete(companyId);
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
    public UserData createUserData(UserSpec userSpec) throws DataException {
        final String password = userSpec.getPassword();
        if (password == null || password.isEmpty()) {
            throw new DataException("Password is required to create a user");
        }
        List<RoleSpec> roleList = userSpec.getRoles();
        if (roleList != null) {
            boolean invalidRole = roleList.stream().anyMatch(roleData -> !roleData.isValid());
            if (invalidRole) {
                throw new DataException("Role ID and permissions are required to create a role");
            }
            for (var roleData : roleList) {
                roleService.create(roleData);
            }
        }
        var userData = userService.create(userSpec);
        if (userSpec.getIsCompanyAuthAllowList() != null && userSpec.getIsCompanyAuthAllowList()) {
            var companyAuthAllowListSpec = new CompanyAuthAllowListSpec();
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
        if (user.getRoles() != null && !user.getRoles().isEmpty()) {
            for (String roleId : user.getRoles()) {
                roleService.delete(roleId);
            }
        }
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            var allowListId = companyAuthAllowListService.getAuthId(user.getEmail());
            if (companyAuthAllowListService.getAuthId(user.getEmail()) != null) {
                companyAuthAllowListService.delete(allowListId);
            }
        }
        return this.userService.delete(userId);
    }

    @Override
    public IdentityData createIdentityData(IdentitySpec identitySpec) throws DataException {
        if (identitySpec.getUserId() == null || identitySpec.getUserId().isEmpty()) {
            throw new DataException("User Id is required to create an identity");
        }
        if (identitySpec.getEmail() == null || identitySpec.getEmail().isEmpty()) {
            throw new DataException("Email is required to create an identity");
        }
        if (identitySpec.getVerificationSource() == null
                || identitySpec.getVerificationSource().isEmpty()) {
            throw new DataException("Verification source is required to create an identity");
        }
        try {
            var identityData = identityService.create(identitySpec);
            userService.updateUserWithOneLogin(identitySpec.getUserId());
            return identityData;
        } catch (Exception ex) {
            throw new DataException("Error creating identity", ex);
        }
    }

    @Override
    public boolean deleteIdentityData(String identityId) throws DataException {
        try {
            return identityService.delete(identityId);
        } catch (Exception ex) {
            throw new DataException("Error deleting identity", ex);
        }
    }

    @Override
    public AcspMembersData createAcspMembersData(final AcspMembersSpec spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create an ACSP member");
        }

        var acspProfileSpec = new AcspProfileSpec();
        if (spec.getAcspProfile() != null) {
            acspProfileSpec = spec.getAcspProfile();
        }

        try {
            var acspProfileData = createAcspProfile(acspProfileSpec);
            spec.setAcspNumber(acspProfileData.getAcspNumber());

            AcspMembersData createdMember = createAcspMember(spec);

            return new AcspMembersData(
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

    private AcspProfileData createAcspProfile(AcspProfileSpec acspProfileSpec)
            throws DataException {
        try {
            return this.acspProfileService.create(acspProfileSpec);
        } catch (Exception ex) {
            throw new DataException("Error creating ACSP profile", ex);
        }
    }

    private AcspMembersData createAcspMember(AcspMembersSpec spec) throws DataException {
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
    public CertificatesData createCertificatesData(
            final CertificatesSpec spec) throws DataException {
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
    public CertificatesData createCertifiedCopiesData(
        final CertifiedCopiesSpec spec) throws DataException {
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
    public CertificatesData createMissingImageDeliveriesData(
        final MissingImageDeliveriesSpec spec) throws DataException {
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
    public CombinedSicActivitiesData createCombinedSicActivitiesData(
        final CombinedSicActivitiesSpec spec) throws DataException {
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
    public AccountPenaltiesData getAccountPenaltiesData(String id)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(id);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesData updateAccountPenaltiesData(
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
    public AccountPenaltiesData createPenaltyData(PenaltySpec penaltySpec) throws DataException {
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
    public PostcodesData getPostcodes(String country) throws DataException {
        try {
            List<Postcodes> postcodes = postcodeService.get(country);
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

    private static List<PostcodesData> getPostCodesData(List<Postcodes> postcodes) {
        List<PostcodesData> postcodesDataList = new ArrayList<>();
        for (Postcodes postcode : postcodes) {
            var postcodeData = new PostcodesData(
                    postcode.getBuildingNumber() != null ? postcode
                            .getBuildingNumber().intValue() : null,
                    postcode.getThoroughfareName() + " " + postcode.getThoroughfareDescriptor(),
                    postcode.getDependentLocality(),
                    postcode.getLocalityPostTown(),
                    postcode.getPretty()
            );
            postcodesDataList.add(postcodeData);
        }
        return postcodesDataList;
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
    public UserCompanyAssociationData
            createUserCompanyAssociationData(UserCompanyAssociationSpec spec)
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
            UserCompanyAssociationData createdAssociation =
                    userCompanyAssociationService.create(spec);

            return new UserCompanyAssociationData(
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


    public TransactionsData createTransactionData(TransactionsSpec transactionsSpec)
            throws DataException {
        try {
            LOG.info("Creating Txn for User Id: " + transactionsSpec.getUserId());
            return transactionService.create(transactionsSpec);
        } catch (Exception ex) {
            LOG.error("Failed to create Transaction for User Id: "
                    + transactionsSpec.getUserId());
            throw new DataException("Error creating transaction", ex);
        }
    }
}