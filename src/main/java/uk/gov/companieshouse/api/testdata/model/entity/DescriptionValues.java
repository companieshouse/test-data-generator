package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class DescriptionValues {
    @Field("appointment_date")
    private Instant appointmentDate;

    @Field("officer_name")
    private String officerName;

    @Field("charge_number")
    private String chargeNumber;

    @Field("capital")
    private List<Capital> capital;

    @Field("date")
    private Instant date;

    @Field("made_up_date")
    private Instant madeUpdate;

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

    public String getChargeNumber() {
        return chargeNumber;
    }

    public void setChargeNumber(String chargeNumber) {
        this.chargeNumber = chargeNumber;
    }

    public List<Capital> getCapital() {
        return capital;
    }

    public void setCapital(List<Capital> capital) {
        this.capital = capital;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Instant getMadeUpDate() {
        return madeUpdate;
    }

    public void setMadeUpDate(Instant madeUpdate) {
        this.madeUpdate = madeUpdate;
    }
}
