
package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Links {

    @Field("filing_history")
    private String filingHistory;
    @Field("officers")
    private String officers;
    @Field("persons_with_significant_control_statement")
    private String personsWithSignificantControlStatement;
    @Field("self")
    private String self;
    @Field("officer.self")
    private String officerSelf;
    @Field("officer.officerAppointments")
    private String officerAppointments;
    @Field("company")
    private String company;
    @Field("registers")
    private String registers;

    public String getFilingHistory() {
        return filingHistory;
    }

    public void setFilingHistory(String filingHistory) {
        this.filingHistory = filingHistory;
    }

    public String getOfficers() {
        return officers;
    }

    public void setOfficers(String officers) {
        this.officers = officers;
    }

    public String getPersonsWithSignificantControlStatement() {
        return personsWithSignificantControlStatement;
    }

    public void setPersonsWithSignificantControlStatement(
            String personsWithSignificantControlStatement) {
        this.personsWithSignificantControlStatement = personsWithSignificantControlStatement;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }

    public String getOfficerSelf() {
        return officerSelf;
    }

    public void setOfficerSelf(String officerSelf) {
        this.officerSelf = officerSelf;
    }

    public String getOfficerAppointments() {
        return officerAppointments;
    }

    public void setOfficerAppointments(String officerAppointments) {
        this.officerAppointments = officerAppointments;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getRegisters() {
        return registers;
    }

    public void setRegisters(String registers) {
        this.registers = registers;
    }
}
