package com.poc.testdata.model.Officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Address;
import com.poc.testdata.model.DateOfBirth;
import com.poc.testdata.model.Links;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

public class OfficerItem {


    @Field("address")
    private Address address;
    @Field("appointed_on")
    private Date appointedOn;
    @Field("country_of_residence")
    private String countryOfResidents;
    @Field("date_of_birth")
    private DateOfBirth dateOfBirth;
    @Field("former_names")
    private List<FormerName> formerNames = null;
    @Field("identification")
    private Identification identification;
    @Field("links")
    private Links links;
    @Field("name")
    private String name;
    @Field("nationality")
    private String nationality;
    @Field("occupation")
    private String occupation;
    @Field("officer_role")
    private String officerRole;
    @Field("resigned_on")
    private Date resignedOn;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getAppointedOn() {
        return appointedOn;
    }

    public void setAppointedOn(Date appointedOn) {
        this.appointedOn = appointedOn;
    }

    public String getCountryOfResidents() {
        return countryOfResidents;
    }

    public void setCountryOfResidents(String countryOfResidents) {
        this.countryOfResidents = countryOfResidents;
    }

    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public List<FormerName> getFormerNames() {
        return formerNames;
    }

    public void setFormerNames(List<FormerName> formerNames) {
        this.formerNames = formerNames;
    }

    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getOfficerRole() {
        return officerRole;
    }

    public void setOfficerRole(String officerRole) {
        this.officerRole = officerRole;
    }

    public Date getResignedOn() {
        return resignedOn;
    }

    public void setResignedOn(Date resignedOn) {
        this.resignedOn = resignedOn;
    }
}
