package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appointments")
public class AppointmentsData {

    @Id
    @Field("_id")
    private String id;

    @Field("created.at")
    private Instant created;

    @Field("internal_id")
    private String internalId;

    @Field("appointment_id")
    private String appointmentId;

    @Field("data.nationality")
    private String nationality;

    @Field("data.occupation")
    private String occupation;

    @Field("data.service_address_is_same_as_registered_office_address")
    private boolean serviceAddressIsSameAsRegisteredOfficeAddress;

    @Field("data.country_of_residence")
    private String countryOfResidence;

    @Field("data.updated_at")
    private Instant updatedAt;

    @Field("data.forename")
    private String forename;

    @Field("data.appointed_on")
    private Instant appointedOn;

    @Field("data.officer_role")
    private String officerRole;

    @Field("data.is_secure_officer")
    private Boolean isSecureOfficer;

    @Field("data.etag")
    private String etag;

    @Field("data.service_address")
    private Address serviceAddress;

    @Field("data.company_number")
    private String dataCompanyNumber;

    @Field("data.links")
    private Links links;

    @Field("data.surname")
    private String surname;

    @Field("sensitive_data.date_of_birth")
    private Instant dateOfBirth;

    @Field("company_name")
    private String companyName;

    @Field("company_status")
    private String companyStatus;

    @Field("officer_id")
    private String officerId;

    @Field("company_number")
    private String companyNumber;

    @Field("updated.at")
    private Instant updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public void setServiceAddressIsSameAsRegisteredOfficeAddress(boolean value) {
        this.serviceAddressIsSameAsRegisteredOfficeAddress = value;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setAppointedOn(Instant appointedOn) {
        this.appointedOn = appointedOn;
    }

    public void setOfficerRole(String officerRole) {
        this.officerRole = officerRole;
    }

    public void setSecureOfficer(Boolean secureOfficer) {
        isSecureOfficer = secureOfficer;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setDataCompanyNumber(String dataCompanyNumber) {
        this.dataCompanyNumber = dataCompanyNumber;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setDateOfBirth(Instant dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public void setOfficerId(String officerId) {
        this.officerId = officerId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    public String getInternalId() {
        return internalId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public String getNationality() {
        return nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public boolean isServiceAddressIsSameAsRegisteredOfficeAddress() {
        return serviceAddressIsSameAsRegisteredOfficeAddress;
    }

    public Instant getAppointedOn() {
        return appointedOn;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public Boolean getIsSecureOfficer() {
        return isSecureOfficer;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public String getDataCompanyNumber() {
        return dataCompanyNumber;
    }

    public Instant getDateOfBirth() {
        return dateOfBirth;
    }

    public String getOfficerId() {
        return officerId;
    }

    public static class ServiceAddress {
        @Field("address_line_1")
        private String addressLine1;
        @Field("country")
        private String country;
        @Field("postal_code")
        private String postalCode;
        @Field("locality")
        private String locality;
        @Field("region")
        private String region;
        @Field("premises")
        private String premises;

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }

        public String getLocality() {
            return locality;
        }

        public String getRegion() {
            return region;
        }

        public String getPremises() {
            return premises;
        }
    }

    public static class Links {
        @Field("officer")
        private OfficerLinks officer;

        @Field("self")
        private String self;

        public void setOfficer(OfficerLinks officer) {
            this.officer = officer;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getSelf() {
            return self;
        }

        public OfficerLinks getOfficer() {
            return officer;
        }
    }

    public static class OfficerLinks {
        @Field("appointments")
        private String appointments;
        @Field("self")
        private String self;

        public void setAppointments(String appointments) {
            this.appointments = appointments;
        }

        public void setSelf(String self) {
            this.self = self;
        }

        public String getAppointments() {
            return appointments;
        }

        public String getSelf() {
            return self;
        }
    }
}