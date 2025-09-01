package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Field;

public class OfficerAppointmentItem {

    @Field("occupation")
    private String occupation;
    @Field("address")
    private Address address;
    @Field("name_elements.forename")
    private String forename;
    @Field("name_elements.surname")
    private String surname;
    @Field("officer_role")
    private String officerRole;
    @Field("is_secure_officer")
    private Boolean isSecureOfficer;
    @Field("links")
    private Links links;
    @Field("country_of_residence")
    private String countryOfResidence;
    @Field("appointed_on")
    private Instant appointedOn;
    @Field("nationality")
    private String nationality;
    @Field("updated_at")
    private Instant updatedAt;
    @Field("name")
    private String name;
    @Field("appointed_to.company_name")
    private String companyName;
    @Field("appointed_to.company_number")
    private String companyNumber;
    @Field("appointed_to.company_status")
    private String companyStatus;

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getOfficerRole() {
        return officerRole;
    }

    public void setOfficerRole(String officerRole) {
        this.officerRole = officerRole;
    }

    public void setSecureOfficer(Boolean secureOfficer) {
        isSecureOfficer = secureOfficer;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public Instant getAppointedOn() {
        return appointedOn;
    }

    public void setAppointedOn(Instant appointedOn) {
        this.appointedOn = appointedOn;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }
}
