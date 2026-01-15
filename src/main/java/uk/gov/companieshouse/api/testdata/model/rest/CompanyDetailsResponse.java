package uk.gov.companieshouse.api.testdata.model.rest;

import uk.gov.companieshouse.api.testdata.model.entity.*;

import java.util.List;

public class CompanyDetailsResponse {
    private CompanyProfile companyProfile;
    private AppointmentsData appointmentsData;
    private CompanyAuthCode companyAuthCode;
    private FilingHistory filingHistory;
    private CompanyMetrics companyMetrics;
    private List<CompanyPscStatement> companyPscStatement;
    private CompanyPscs companyPscs;
    private CompanyRegisters companyRegisters;
    private Disqualifications disqualifications;

    public void setCompanyProfile(CompanyProfile companyProfile) {
        this.companyProfile = companyProfile;
    }

    public void setAppointmentsData(AppointmentsData appointmentsData) {
        this.appointmentsData = appointmentsData;
    }

    public CompanyProfile getCompanyProfile() {
        return companyProfile;
    }

    public AppointmentsData getAppointmentsData() {
        return appointmentsData;
    }

    public Disqualifications getDisqualifications() {
        return disqualifications;
    }

    public void setDisqualifications(Disqualifications disqualifications) {
        this.disqualifications = disqualifications;
    }

    public CompanyRegisters getCompanyRegisters() {
        return companyRegisters;
    }

    public void setCompanyRegisters(CompanyRegisters companyRegisters) {
        this.companyRegisters = companyRegisters;
    }

    public CompanyPscs getCompanyPscs() {
        return companyPscs;
    }

    public void setCompanyPscs(CompanyPscs companyPscs) {
        this.companyPscs = companyPscs;
    }


    public CompanyMetrics getCompanyMetrics() {
        return companyMetrics;
    }

    public void setCompanyMetrics(CompanyMetrics companyMetrics) {
        this.companyMetrics = companyMetrics;
    }

    public FilingHistory getFilingHistory() {
        return filingHistory;
    }

    public void setFilingHistory(FilingHistory filingHistory) {
        this.filingHistory = filingHistory;
    }

    public CompanyAuthCode getCompanyAuthCode() {
        return companyAuthCode;
    }

    public void setCompanyAuthCode(CompanyAuthCode companyAuthCode) {
        this.companyAuthCode = companyAuthCode;
    }

    public List<CompanyPscStatement> getCompanyPscStatement() {
        return companyPscStatement;
    }

    public void setCompanyPscStatement(List<CompanyPscStatement> companyPscStatement) {
        this.companyPscStatement = companyPscStatement;
    }
}
