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
import uk.gov.companieshouse.api.testdata.model.entity.*;
import uk.gov.companieshouse.api.testdata.model.rest.*;
import uk.gov.companieshouse.api.testdata.service.*;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service
public class TestDataServiceImpl implements TestDataService {
  private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

  private static final int COMPANY_NUMBER_LENGTH = 8;

  @Autowired private CompanyProfileService companyProfileService;
  @Autowired private DataService<FilingHistory, CompanySpec> filingHistoryService;
  @Autowired private CompanyAuthCodeService companyAuthCodeService;
  @Autowired private DataService<Appointment, CompanySpec> appointmentService;
  @Autowired private DataService<CompanyMetrics, CompanySpec> companyMetricsService;
  @Autowired private DataService<CompanyPscStatement, CompanySpec> companyPscStatementService;
  @Autowired private DataService<CompanyPscs, CompanySpec> companyPscsService;
  @Autowired private RandomService randomService;
  @Autowired private UserService userService;
  @Autowired private DataService<RoleData, RoleSpec> roleService;

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
      spec.setCompanyNumber(
          companyNumberPrefix
              + randomService.getNumber(COMPANY_NUMBER_LENGTH - companyNumberPrefix.length()));
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
}
