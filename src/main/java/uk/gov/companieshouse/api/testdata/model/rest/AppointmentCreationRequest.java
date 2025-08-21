package uk.gov.companieshouse.api.testdata.model.rest;

import java.time.Instant;

public class AppointmentCreationRequest {
    private CompanySpec spec;
    private String companyNumber;
    private String countryOfResidence;
    private String internalId;
    private String officerId;
    private Instant dateTimeNow;
    private Instant appointedOn;
    private String appointmentId;

    private AppointmentCreationRequest() {
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AppointmentCreationRequest request = new AppointmentCreationRequest();

        public Builder spec(CompanySpec spec) {
            request.spec = spec;
            return this;
        }

        public Builder companyNumber(String companyNumber) {
            request.companyNumber = companyNumber;
            return this;
        }

        public Builder countryOfResidence(String countryOfResidence) {
            request.countryOfResidence = countryOfResidence;
            return this;
        }

        public Builder internalId(String internalId) {
            request.internalId = internalId;
            return this;
        }

        public Builder officerId(String officerId) {
            request.officerId = officerId;
            return this;
        }

        public Builder dateTimeNow(Instant dateTimeNow) {
            request.dateTimeNow = dateTimeNow;
            return this;
        }

        public Builder appointedOn(Instant appointedOn) {
            request.appointedOn = appointedOn;
            return this;
        }

        public Builder appointmentId(String appointmentId) {
            request.appointmentId = appointmentId;
            return this;
        }

        public AppointmentCreationRequest build() {
            return request;
        }
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