package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class OriginalValues {
    @Field("appointment_date")
    private Instant appointmentDate;

    @Field("officer_name")
    private String officerName;

    public Instant getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Instant appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getOfficerName() {
        return officerName;
    }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }
}
