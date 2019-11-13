package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "appointments")
public class Appointment {
    @Id
    @Field("id")
    private String id;

    @Field("created.at")
    private LocalDateTime created;

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
    private LocalDateTime updatedAt;

    @Field("data.forename")
    private String forename;

    @Field("data.appointed_on")
    private LocalDate appointedOn;

    @Field("data.officer_role")
    private String officerRole;

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

    @Field("data.date_of_birth")
    private LocalDate dateOfBirth;

    @Field("company_name")
    private String companyName;

    @Field("company_status")
    private String companyStatus;

    @Field("officer_id")
    private String officerId;

    @Field("company_number")
    private String companyNumber;

    @Field("updated.at")
    private LocalDateTime updated;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public String getInternalId() {
        return internalId;
    }

    public void setInternalId(String internalId) {
        this.internalId = internalId;
    }

    public String getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public boolean isServiceAddressIsSameAsRegisteredOfficeAddress() {
        return serviceAddressIsSameAsRegisteredOfficeAddress;
    }

    public void setServiceAddressIsSameAsRegisteredOfficeAddress(boolean serviceAddressIsSameAsRegisteredOfficeAddress) {
        this.serviceAddressIsSameAsRegisteredOfficeAddress = serviceAddressIsSameAsRegisteredOfficeAddress;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public LocalDate getAppointedOn() {
        return appointedOn;
    }

    public void setAppointedOn(LocalDate appointedOn) {
        this.appointedOn = appointedOn;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public void setOfficerRole(String officerRole) {
        this.officerRole = officerRole;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public String getDataCompanyNumber() {
        return dataCompanyNumber;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
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

    public String getOfficerId() {
        return officerId;
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

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}
