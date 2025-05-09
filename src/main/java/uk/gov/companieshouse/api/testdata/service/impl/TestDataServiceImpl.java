package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.AccountPenaltiesData;

import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesData;
import uk.gov.companieshouse.api.testdata.model.rest.CertificatesSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyAuthAllowListSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UpdateAccountPenaltiesRequest;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;
import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.AccountPenaltiesService;
import uk.gov.companieshouse.api.testdata.repository.CertificatesRepository;
import uk.gov.companieshouse.api.testdata.service.AppealsService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthAllowListService;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.api.testdata.service.DataService;
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
    private DataService<FilingHistory, CompanySpec> filingHistoryService;
    @Autowired
    private CompanyAuthCodeService companyAuthCodeService;
    @Autowired
    private DataService<List<Appointment>, CompanySpec> appointmentService;
    @Autowired
    private DataService<CompanyMetrics, CompanySpec> companyMetricsService;
    @Autowired
    private DataService<CompanyPscStatement, CompanySpec> companyPscStatementService;
    @Autowired
    private DataService<CompanyPscs, CompanySpec> companyPscsService;
    @Autowired
    private RandomService randomService;
    @Autowired
    private UserService userService;
    @Autowired
    private DataService<AcspMembersData, AcspMembersSpec> acspMembersService;
    @Autowired
    private DataService<CertificatesData, CertificatesSpec> certificatesService;
    @Autowired
    private AcspMembersRepository acspMembersRepository;
    @Autowired
    private CertificatesRepository certificatesRepository;
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
    private CompanySearchService companySearchService;
    @Autowired
    private AccountPenaltiesService accountPenaltiesService;

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
        final String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix(spec);

        do {
            // company number format: PP+123456 (Prefix either 0 or 2 chars, example uses 2 chars)
            spec.setCompanyNumber(companyNumberPrefix
                    + randomService
                    .getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));

        try {
            companyProfileService.create(spec);
            filingHistoryService.create(spec);
            appointmentService.create(spec);
            var authCode = companyAuthCodeService.create(spec);
            companyMetricsService.create(spec);
            companyPscStatementService.create(spec);
            companyPscsService.create(spec);

            if (spec.getRegisters() != null && !spec.getRegisters().isEmpty()) {
                this.companyRegistersService.create(spec);
            }

            String companyUri = this.apiUrl + "/company/" + spec.getCompanyNumber();

            var companyData = new CompanyData(spec.getCompanyNumber(),
                    authCode.getAuthCode(), companyUri);

            // Add company to the elastic search index
            if (isElasticSearchDeployed) {
                this.companySearchService.addCompanyIntoElasticSearchIndex(companyData);
            }
            return companyData;
        } catch (Exception ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("company number", spec.getCompanyNumber());
            LOG.error("Rolling back creation of company", data);
            // Rollback all successful insertions
            deleteCompanyData(spec.getCompanyNumber());
            throw new DataException(ex);
        }
    }

    @Override
    public void deleteCompanyData(String companyId) throws DataException {
        List<Exception> suppressedExceptions = new ArrayList<>();

        try {
            this.companyProfileService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.filingHistoryService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyAuthCodeService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.appointmentService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscStatementService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyPscsService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }
        try {
            this.companyMetricsService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        try {
            this.companyRegistersService.delete(companyId);
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        if (isElasticSearchDeployed) {
            try {
                this.companySearchService.deleteCompanyFromElasticSearchIndex(companyId);
            } catch (Exception de) {
                suppressedExceptions.add(de);
            }
        }

        if (!suppressedExceptions.isEmpty()) {
            DataException ex = new DataException("Error deleting company data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
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
    public CertificatesData createCertificatesData(final CertificatesSpec spec) throws DataException {
        if (spec.getUserId() == null) {
            throw new DataException("User ID is required to create a certificates");
        }

        try {
            CertificatesData createdCertificates = certificatesService.create(spec);

            return new CertificatesData(
                    createdCertificates.getId(),
                    createdCertificates.getCreatedAt(),
                    createdCertificates.getUpdatedAt()
            );

        } catch (Exception ex) {
            throw new DataException("Error creating certificates", ex);
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
    public boolean deleteAppealsData(String companyNumber, String penaltyReference)
            throws DataException {
        try {
            return appealsService.delete(companyNumber, penaltyReference);
        } catch (Exception ex) {
            throw new DataException("Error deleting appeals data", ex);
        }
    }

    @Override
    public AccountPenaltiesData getAccountPenaltyData(String companyCode, String customerCode,
            String penaltyReference) throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalty(companyCode, customerCode,
                    penaltyReference);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalty - not found");
        }
    }

    @Override
    public AccountPenaltiesData getAccountPenaltiesData(String companyCode, String customerCode)
            throws NoDataFoundException {
        try {
            return accountPenaltiesService.getAccountPenalties(companyCode, customerCode);
        } catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error retrieving account penalties - not found");
        }
    }

    @Override
    public AccountPenaltiesData updateAccountPenaltiesData(String penaltyRef,
            UpdateAccountPenaltiesRequest request) throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.updateAccountPenalties(penaltyRef, request);
        }   catch (NoDataFoundException ex) {
                throw new NoDataFoundException("Error updating account penalties - not found");
        } catch (Exception ex) {
                throw new DataException("Error updating account penalties", ex);
        }
    }

    @Override
    public ResponseEntity<Void> deleteAccountPenaltiesData(String companyCode, String customerCode)
            throws NoDataFoundException, DataException {
        try {
            return accountPenaltiesService.deleteAccountPenalties(companyCode, customerCode);
        }  catch (NoDataFoundException ex) {
            throw new NoDataFoundException("Error deleting account penalties - not found");
        }
        catch (Exception ex) {
            throw new DataException("Error deleting account penalties", ex);
        }
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
}
