package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.List;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyMetrics;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscStatement;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyPscs;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyRegisters;
import uk.gov.companieshouse.api.testdata.model.entity.Disqualifications;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;

public class PopulatedCompanyDetailsResponse {
    private CompanyProfile companyProfile;
    private AppointmentsResultData appointmentsData;
    private CompanyAuthCode companyAuthCode;
    private FilingHistory filingHistory;
    private CompanyMetrics companyMetrics;
    private List<CompanyPscStatement> companyPscStatement;
    private List<CompanyPscs> companyPscs;
    private CompanyRegisters companyRegisters;
    private Disqualifications disqualifications;

    public void setCompanyProfile(CompanyProfile companyProfile) {
        this.companyProfile = companyProfile;
    }

    public void setAppointmentsData(AppointmentsResultData appointmentsData) {
        this.appointmentsData = appointmentsData;
    }

    public CompanyProfile getCompanyProfile() {
        return companyProfile;
    }

    public AppointmentsResultData getAppointmentsData() {
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

    public List<CompanyPscs> getCompanyPscs() {
        return companyPscs;
    }

    public void setCompanyPscs(List<CompanyPscs> companyPscs) {
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
