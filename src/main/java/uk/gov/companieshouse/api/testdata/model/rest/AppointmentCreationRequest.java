package uk.gov.companieshouse.api.testdata.model.rest;

import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import java.time.Instant;

public class AppointmentCreationRequest {
    private final CompanySpec spec;
    private final String companyNumber;
    private final String countryOfResidence;
    private final String internalId;
    private final String officerId;
    private final Instant dateTimeNow;
    private final Instant appointedOn;
    private final String appointmentId;

    public AppointmentCreationRequest(CompanySpec spec, String companyNumber, String countryOfResidence,
                                      String internalId, String officerId, Instant dateTimeNow,
                                      Instant appointedOn, String appointmentId) {
        this.spec = spec;
        this.companyNumber = companyNumber;
        this.countryOfResidence = countryOfResidence;
        this.internalId = internalId;
        this.officerId = officerId;
        this.dateTimeNow = dateTimeNow;
        this.appointedOn = appointedOn;
        this.appointmentId = appointmentId;
    }


    public CompanySpec getSpec() {
        return spec;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public String getInternalId() {
        return internalId;
    }

    public String getOfficerId() {
        return officerId;
    }

    public Instant getDateTimeNow() {
        return dateTimeNow;
    }

    public Instant getAppointedOn() {
        return appointedOn;
    }

    public String getAppointmentId() {
        return appointmentId;
    }
}