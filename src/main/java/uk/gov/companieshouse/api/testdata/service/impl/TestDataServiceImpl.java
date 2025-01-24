package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;

import uk.gov.companieshouse.api.testdata.model.entity.Appointment;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspMembersSpec;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;
import uk.gov.companieshouse.api.testdata.model.rest.CompanyData;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.IdentityData;
import uk.gov.companieshouse.api.testdata.model.rest.IdentitySpec;
import uk.gov.companieshouse.api.testdata.model.rest.RoleData;
import uk.gov.companieshouse.api.testdata.model.rest.RoleSpec;
import uk.gov.companieshouse.api.testdata.model.rest.UserData;
import uk.gov.companieshouse.api.testdata.model.rest.UserSpec;

import uk.gov.companieshouse.api.testdata.repository.AcspMembersRepository;
import uk.gov.companieshouse.api.testdata.service.CompanyAuthCodeService;
import uk.gov.companieshouse.api.testdata.service.CompanyProfileService;
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
    private DataService<Appointment, CompanySpec> appointmentService;
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
    private AcspMembersRepository acspMembersRepository;
    @Autowired
    private DataService<RoleData, RoleSpec> roleService;
    @Autowired
    private DataService<IdentityData, IdentitySpec> identityService;
    @Autowired
    private DataService<AcspProfileData, AcspProfileSpec> acspProfileService;

    @Value("${api.url}")
    private String apiUrl;

    void setAPIUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public CompanyData createCompanyData(final CompanySpec spec) throws DataException {
        if (spec == null) {
            throw new IllegalArgumentException("CompanySpec can not be null");
        }
        final String companyNumberPrefix = spec.getJurisdiction().getCompanyNumberPrefix();

        do {
            // company number format: PP+123456 (Prefix either 0 or 2 chars, example uses 2 chars)
            spec.setCompanyNumber(companyNumberPrefix
                    + randomService
                    .getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
        } while (companyProfileService.companyExists(spec.getCompanyNumber()));

        try {
            this.companyProfileService.create(spec);
            this.filingHistoryService.create(spec);
            this.appointmentService.create(spec);
            CompanyAuthCode authCode = this.companyAuthCodeService.create(spec);
            this.companyMetricsService.create(spec);
            this.companyPscStatementService.create(spec);
            this.companyPscsService.create(spec);
            this.companyPscsService.create(spec);
            this.companyPscsService.create(spec);

            String companyUri = this.apiUrl + "/company/" + spec.getCompanyNumber();
            return new CompanyData(spec.getCompanyNumber(), authCode.getAuthCode(), companyUri);
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
        return userService.create(userSpec);
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
            return identityService.create(identitySpec);
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

        try {
            var acspProfileSpec = new AcspProfileSpec();
            if (spec.getAcspProfile() != null) {
                acspProfileSpec.setStatus(spec.getAcspProfile().getStatus());
            }
            if (spec.getAcspProfile() != null) {
                acspProfileSpec.setType(spec.getAcspProfile().getType());
            }

            AcspProfileData acspProfileData;
            try {
                acspProfileData = this.acspProfileService.create(acspProfileSpec);
            } catch (Exception ex) {
                throw new DataException("Error creating ACSP profile", ex);
            }

            spec.setAcspNumber(acspProfileData.getAcspNumber());

            AcspMembersData createdMember;
            try {
                createdMember = this.acspMembersService.create(spec);
            } catch (Exception ex) {
                throw new DataException("Error creating ACSP member", ex);
            }

            return new AcspMembersData(
                    createdMember.getAcspMemberId(),
                    createdMember.getAcspNumber(),
                    createdMember.getUserId(),
                    createdMember.getStatus(),
                    createdMember.getUserRole()
            );

        } catch (Exception ex) {
            throw new DataException(ex);
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

                try {
                    acspMembersService.delete(acspMemberId);
                } catch (Exception ex) {
                    suppressedExceptions.add(new DataException("Error deleting ACSP member", ex));
                }

                try {
                    this.acspProfileService.delete(acspNumber);
                } catch (Exception ex) {
                    suppressedExceptions.add(new DataException("Error deleting ACSP profile", ex));
                }
            } else {
                return false;
            }
        } catch (Exception de) {
            suppressedExceptions.add(de);
        }

        if (!suppressedExceptions.isEmpty()) {
            DataException ex = new DataException("Error deleting acsp member's data");
            suppressedExceptions.forEach(ex::addSuppressed);
            throw ex;
        }

        return true;
    }

}
