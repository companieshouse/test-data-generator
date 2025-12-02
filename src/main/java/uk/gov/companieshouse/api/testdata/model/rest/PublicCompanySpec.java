package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Requirements a new company must meet for public.
 */
public class PublicCompanySpec {

    @JsonProperty
    private Jurisdiction jurisdiction;

    @JsonProperty("company_status")
    @Pattern(regexp = "active|inactive|closed|dissolved|administration|open|insolvency-proceedings|liquidation|converted-closed|receivership|registered|removed|voluntary-arrangement", message = "Invalid company status")
    private String companyStatus;

    @JsonProperty("type")
    private CompanyType companyType;

    @JsonProperty("sub_type")
    @Pattern(regexp = "community-interest-company|private-fund-limited-partnership",
            message = "Invalid company subtype")
    private String subType;

    @JsonProperty("has_super_secure_pscs")
    private Boolean hasSuperSecurePscs;

    @JsonProperty("is_secure_officer")
    private Boolean isSecureOfficer;

    @JsonProperty
    @Valid
    @Size(max = 20, message = "Registers must not exceed 20")
    private List<RegistersSpec> registers;

    @JsonProperty("company_status_detail")
    @Pattern(regexp = "active|dissolved|converted-closed|transferred-from-uk|active-proposal-to-strike-off|petition-to-restore-dissolved|transformed-to-se|converted-to-plc|converted-to-uk-societas|converted-to-ukeig",
            message = "Invalid company status detail")
    private String companyStatusDetail;

    @Size(max = 20, message = "Filing history items must not exceed 20")
    @Valid
    @JsonProperty("filing_history")
    private List<FilingHistorySpec> filingHistoryList;

    @Min(value = 1, message = "Number of appointments must be at least 1")
    @Max(value = 20, message = "Number of appointments must not exceed 20")
    @JsonProperty("number_of_appointments")
    private int numberOfAppointments = 1;

    @Size(max = 20, message = "Officer roles must not exceed 20")
    @Valid
    @JsonProperty("officer_roles")
    private List<OfficerRoles> officerRoles;

    @JsonProperty("accounts_due_status")
    @Pattern(regexp = "overdue|due-soon", message = "Invalid accounts due status")
    private String accountsDueStatus;

    @Min(value = 1, message = "Number of PSCs must be at least 1")
    @Max(value = 20, message = "Number of PSCs must not exceed 20")
    @JsonProperty("number_of_psc")
    private Integer numberOfPsc;

    @Size(max = 20, message = "PSC types must not exceed 20")
    @Valid
    @JsonProperty("psc_type")
    private List<PscType> pscType;

    @JsonProperty("psc_active")
    private Boolean pscActive;

    @Min(value = 0, message = "Withdrawn statements must be at least 0")
    @Max(value = 20, message = "Withdrawn statements must not exceed 20")
    @JsonProperty("withdrawn_statements")
    private Integer withdrawnStatements;

    @Min(value = 0, message = "Active statements must be at least 0")
    @Max(value = 20, message = "Active statements must not exceed 20")
    @JsonProperty("active_statements")
    private Integer activeStatements;

    @JsonProperty("has_uk_establishment")
    private Boolean hasUkEstablishment;

    @JsonProperty("registered_office_is_in_dispute")
    private Boolean registeredOfficeIsInDispute;

    @JsonProperty("undeliverable_registered_office_address")
    private Boolean undeliverableRegisteredOfficeAddress;

    @JsonProperty("foreign_company_legal_form")
    private Boolean foreignCompanyLegalForm;

    public PublicCompanySpec() {
        jurisdiction = Jurisdiction.ENGLAND_WALES;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public CompanyType getCompanyType() {
        return companyType;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public void setCompanyType(CompanyType companyType) {
        this.companyType = companyType;
    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    public Boolean getHasSuperSecurePscs() {
        return hasSuperSecurePscs;
    }

    public void setHasSuperSecurePscs(Boolean hasSuperSecurePscs) {
        this.hasSuperSecurePscs = hasSuperSecurePscs;
    }

    public int getNumberOfAppointments() {
        return numberOfAppointments;
    }

    public void setNumberOfAppointments(int numberOfAppointments) {
        this.numberOfAppointments = numberOfAppointments;
    }

    public List<OfficerRoles> getOfficerRoles() {
        return officerRoles;
    }

    public void setOfficerRoles(List<OfficerRoles> officerRoles) {
        this.officerRoles = officerRoles;
    }

    public List<RegistersSpec> getRegisters() {
        return registers;
    }

    public void setRegisters(List<RegistersSpec> registers) {
        this.registers = registers;
    }

    public String getCompanyStatusDetail() {
        return companyStatusDetail;
    }

    public void setCompanyStatusDetail(String companyStatusDetail) {
        this.companyStatusDetail = companyStatusDetail;
    }

    public List<FilingHistorySpec> getFilingHistoryList() {
        return filingHistoryList;
    }

    public void setFilingHistoryList(List<FilingHistorySpec> filingHistoryList) {
        this.filingHistoryList = filingHistoryList;
    }

    public String getAccountsDueStatus() {
        return accountsDueStatus;
    }

    public void setAccountsDueStatus(String accountsDueStatus) {
        this.accountsDueStatus = accountsDueStatus;
    }

    public Integer getNumberOfPsc() {
        return numberOfPsc;
    }

    public void setNumberOfPsc(Integer numberOfPsc) {
        this.numberOfPsc = numberOfPsc;
    }

    public List<PscType> getPscType() {
        return pscType;
    }

    public void setPscType(List<PscType> pscType) {
        this.pscType = pscType;
    }

    public Boolean getHasUkEstablishment() {
        return hasUkEstablishment;
    }

    public void setHasUkEstablishment(Boolean hasUkEstablishment) {
        this.hasUkEstablishment = hasUkEstablishment;
    }

    public Boolean getRegisteredOfficeIsInDispute() {
        return registeredOfficeIsInDispute;
    }

    public void setRegisteredOfficeIsInDispute(Boolean registeredOfficeIsInDispute) {
        this.registeredOfficeIsInDispute = registeredOfficeIsInDispute;
    }

    public Boolean getUndeliverableRegisteredOfficeAddress() {
        return undeliverableRegisteredOfficeAddress;
    }

    public void setUndeliverableRegisteredOfficeAddress(Boolean undeliverableRegisteredOfficeAddress) {
        this.undeliverableRegisteredOfficeAddress = undeliverableRegisteredOfficeAddress;
    }

    public Boolean getPscActive() {
        return pscActive;
    }

    public void setPscActive(Boolean pscActive) {
        this.pscActive = pscActive;
    }

    public Integer getWithdrawnStatements() {
        return withdrawnStatements;
    }

    public void setWithdrawnStatements(Integer withdrawnStatements) {
        this.withdrawnStatements = withdrawnStatements;
    }

    public Integer getActiveStatements() {
        return activeStatements;
    }

    public void setActiveStatements(Integer activeStatements) {
        this.activeStatements = activeStatements;
    }

    public Boolean getSecureOfficer() {
        return isSecureOfficer;
    }

    public void setSecureOfficer(Boolean secureOfficer) {
        isSecureOfficer = secureOfficer;
    }

    public Boolean getForeignCompanyLegalForm() {
        return foreignCompanyLegalForm;
    }

    public void setForeignCompanyLegalForm(Boolean foreignCompanyLegalForm) {
        this.foreignCompanyLegalForm = foreignCompanyLegalForm;
    }
}

