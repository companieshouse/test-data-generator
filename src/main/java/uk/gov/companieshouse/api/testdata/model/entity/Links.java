
package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Links {

    @Field("filing_history")
    private String filingHistory;
    @Field("officers")
    private String officers;
    @Field("persons_with_significant_control")
    private String personsWithSignificantControl;
    @Field("self")
    private String self;
    @Field("officer.self")
    private String officerSelf;
    @Field("officer.appointments")
    private String appointments;

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

    public String getPersonsWithSignificantControl() {
        return personsWithSignificantControl;
    }

    public void setPersonsWithSignificantControl(String personsWithSignificantControl) {
        this.personsWithSignificantControl = personsWithSignificantControl;
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

    public String getAppointments() {
        return appointments;
    }

    public void setAppointments(String appointments) {
        this.appointments = appointments;
    }
}
