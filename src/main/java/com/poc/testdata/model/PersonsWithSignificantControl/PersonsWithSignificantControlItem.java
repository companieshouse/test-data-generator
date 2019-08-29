package com.poc.testdata.model.PersonsWithSignificantControl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Address;
import com.poc.testdata.model.DateOfBirth;
import com.poc.testdata.model.Links;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class PersonsWithSignificantControlItem {

    @Field("address")
    private Address address;
    @Field("ceased_on")
    private Date ceasedOn;
    @Field("country_of_residence")
    private String countryOfResidence;
    @Field("date_of_birth")
    private DateOfBirth dateOfBirth;
    @Field("etag")
    private String etag;
    @Field("links")
    private Links links;
    @Field("name")
    private String name;
    @Field("name_elements")
    private NameElement nameElement;
    @Field("nationality")
    private String nationality;
    @Field("natures_of_control")
    private String [] naturesOfControl;
    @Field("notified_on")
    private Date notifiedOn;

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Date getCeasedOn() {
        return ceasedOn;
    }

    public void setCeasedOn(Date ceasedOn) {
        this.ceasedOn = ceasedOn;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
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

    public NameElement getNameElement() {
        return nameElement;
    }

    public void setNameElement(NameElement nameElement) {
        this.nameElement = nameElement;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String[] getNaturesOfControl() {
        return naturesOfControl;
    }

    public void setNaturesOfControl(String[] naturesOfControl) {
        this.naturesOfControl = naturesOfControl;
    }

    public Date getNotifiedOn() {
        return notifiedOn;
    }

    public void setNotifiedOn(Date notifiedOn) {
        this.notifiedOn = notifiedOn;
    }
}
