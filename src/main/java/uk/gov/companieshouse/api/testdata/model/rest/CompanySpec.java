package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;

import java.util.List;

/**
 * Requirements a new company must meet.
 */
public class CompanySpec {

    @JsonProperty
    private Jurisdiction jurisdiction;

    @JsonIgnore
    private String companyNumber;

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

    @JsonProperty
    @Valid
    private List<RegistersSpec> registers;

    @JsonProperty("company_status_detail")
    @Pattern(regexp = "active|dissolved|converted-closed|transferred-from-uk|active-proposal-to-strike-off|petition-to-restore-dissolved|transformed-to-se|converted-to-plc|converted-to-uk-societas|converted-to-ukeig",
            message = "Invalid company status detail")
    private String companyStatusDetail;

    @JsonProperty("filing_history")
    private FilingHistorySpec filingHistory;

    @JsonProperty("number_of_appointments")
    private int numberOfAppointments = 1;

    @JsonProperty("officer_roles")
    private List<OfficerRoles> officerRoles;

    @JsonProperty("accounts_due_status")
    @Pattern(regexp = "overdue|due-soon", message = "Invalid accounts due status")
    private String accountsDueStatus;

    @JsonProperty("number_of_psc")
    private Integer numberOfPsc;

    @JsonProperty("psc_type")
    private List<PscType> pscType;

    @JsonProperty("withdrawn_psc_statements")
    private Integer withdrawnPscStatements;

    @JsonProperty("active_psc_statements")
    private Integer activePscStatements;

    @JsonProperty("has_uk_establishment")
    private Boolean hasUkEstablishment;

    @JsonProperty("registered_office_is_in_dispute")
    private Boolean registeredOfficeIsInDispute;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("is_company_number_padding")
    private Boolean isPaddingCompanyNumber;

    public CompanySpec() {
        jurisdiction = Jurisdiction.ENGLAND_WALES;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
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

    public FilingHistorySpec getFilingHistory() {
        return filingHistory;
    }

    public void setFilingHistory(FilingHistorySpec filingHistory) {
        this.filingHistory = filingHistory;
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

    public Integer getWithdrawnPscStatements() {
        return withdrawnPscStatements;
    }

    public void setWithdrawnPscStatements(Integer withdrawnPscStatements) {
        this.withdrawnPscStatements = withdrawnPscStatements;
    }

    public Integer getActivePscStatements() {
        return activePscStatements;
    }

    public void setActivePscStatements(Integer activePscStatements) {
        this.activePscStatements = activePscStatements;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public Boolean getIsPaddingCompanyNumber() {
        return isPaddingCompanyNumber;
    }

    public void setIsPaddingCompanyNumber(Boolean isPaddingCompanyNumber) {
        this.isPaddingCompanyNumber = isPaddingCompanyNumber;
    }
}

