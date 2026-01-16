package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.*;

import java.util.List;

public class CombinedCompanySpec {

    @JsonProperty("companyProfile")
    private CompanyProfile companyProfile;

    @JsonProperty("appointmentsData")
    private AppointmentsResultData appointmentsData;

    @JsonProperty("companyAuthCode")
    private CompanyAuthCode companyAuthCode;

    @JsonProperty("filingHistory")
    private FilingHistory filingHistory;

    @JsonProperty("companyMetrics")
    private CompanyMetrics companyMetrics;

    @JsonProperty("companyPscStatement")
    private List<CompanyPscStatement> companyPscStatement;

    @JsonProperty("companyPscs")
    private List<CompanyPscs> companyPscs;

    @JsonProperty("companyRegisters")
    private CompanyRegisters companyRegisters;

    @JsonProperty("disqualifications")
    private Disqualifications disqualifications;

    public CompanyProfile getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(CompanyProfile companyProfile) {
        this.companyProfile = companyProfile;
    }

    public AppointmentsResultData getAppointmentsData() {
        return appointmentsData;
    }

    public void setAppointmentsData(AppointmentsResultData appointmentsData) {
        this.appointmentsData = appointmentsData;
    }

    public CompanyAuthCode getCompanyAuthCode() {
        return companyAuthCode;
    }

    public void setCompanyAuthCode(CompanyAuthCode companyAuthCode) {
        this.companyAuthCode = companyAuthCode;
    }

    public FilingHistory getFilingHistory() {
        return filingHistory;
    }

    public void setFilingHistory(FilingHistory filingHistory) {
        this.filingHistory = filingHistory;
    }

    public CompanyMetrics getCompanyMetrics() {
        return companyMetrics;
    }

    public void setCompanyMetrics(CompanyMetrics companyMetrics) {
        this.companyMetrics = companyMetrics;
    }

    public List<CompanyPscStatement> getCompanyPscStatement() {
        return companyPscStatement;
    }

    public void setCompanyPscStatement(List<CompanyPscStatement> companyPscStatement) {
        this.companyPscStatement = companyPscStatement;
    }


    public CompanyRegisters getCompanyRegisters() {
        return companyRegisters;
    }

    public void setCompanyRegisters(CompanyRegisters companyRegisters) {
        this.companyRegisters = companyRegisters;
    }

    public Disqualifications getDisqualifications() {
        return disqualifications;
    }

    public void setDisqualifications(Disqualifications disqualifications) {
        this.disqualifications = disqualifications;
    }

    public List<CompanyPscs> getCompanyPscs() {
        return companyPscs;
    }

    public void setCompanyPscs(List<CompanyPscs> companyPscs) {
        this.companyPscs = companyPscs;
    }
}
