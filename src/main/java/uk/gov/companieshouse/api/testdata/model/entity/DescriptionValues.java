package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

public class DescriptionValues {
    @Field("appointment_date")
    private Instant appointmentDate;

    @Field("officer_name")
    private String officerName;

    @Field("charge_number")
    private String chargeNumber;

    @Field("capital.figure")
    private String figure;

    @Field("capital.currency")
    private String currency;

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

    public String getFigure() { return figure; }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
