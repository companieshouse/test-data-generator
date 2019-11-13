package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AppointmentData {

    @Field("nationality")
    private String nationality;

    @Field("occupation")
    private String occupation;

    @Field("service_address_is_same_as_registered_office_address")
    private boolean serviceAddressIsSameAsRegisteredOfficeAddress;

    @Field("country_of_residence")
    private String countryOfResidence;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    @Field("forename")
    private String forename;

    @Field("appointed_on")
    private LocalDate appointedOn;

    @Field("officer_role")
    private String officerRole;

    @Field("etag")
    private String etag;

    @Field("service_address")
    private Address serviceAddress;

    @Field("company_number")
    private String companyNumber;

    @Field("links")
    private Links links;

    @Field("surname")
    private String surname;

    @Field("date_of_birth")
    private LocalDate dateOfBirth;

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

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
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
}
