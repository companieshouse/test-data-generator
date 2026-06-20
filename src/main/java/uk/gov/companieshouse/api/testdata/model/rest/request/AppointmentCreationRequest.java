package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;

public class AppointmentCreationRequest {
    private CompanyRequest spec;
    @JsonProperty("company_number")
    private String companyNumber;
    @JsonProperty("officer_roles")
    private String officerRoles;
    @JsonProperty("resigned_on")
    private Instant resignedOn;
    @JsonProperty("is_pre_1992_appointment")
    private Boolean isPre1992Appointment;
    @JsonProperty("identification")
    private Identification identification;
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

        public Builder spec(CompanyRequest spec) {
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

    public CompanyRequest getSpec() {
        return spec;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
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

    public String getOfficerRoles() {
        return officerRoles;
    }

    public static class Identification {
        @JsonProperty("identification_type")
        private String identificationType;

        public String getIdentificationType() {
            return identificationType;
        }

        public void setIdentificationType(String identificationType) {
            this.identificationType = identificationType;
        }

        // alias method used in some parts of the codebase
        public String getType() {
            return identificationType;
        }

        public void setType(String type) {
            this.identificationType = type;
        }
    }

    public void setOfficerRoles(String officerRoles) {
        this.officerRoles = officerRoles;
    }

    public Instant getResignedOn() {
        return resignedOn;
    }

    public void setResignedOn(Instant resignedOn) {
        this.resignedOn = resignedOn;
    }

    public Boolean getIsPre1992Appointment() {
        return isPre1992Appointment;
    }

    public void setIsPre1992Appointment(Boolean isPre1992Appointment) {
        this.isPre1992Appointment = isPre1992Appointment;
    }


    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public void setDateTimeNow(Instant dateTimeNow) {
        this.dateTimeNow = dateTimeNow;
    }

    public void setAppointedOn(Instant appointedOn) {
        this.appointedOn = appointedOn;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }


    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }
}

