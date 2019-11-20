package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class OfficerAppointmentItem {


    @Field("address")
    private Address address;
    @Field("appointed_on")
    private Date appointedOn;
    @Field("date_of_birth")
    private DateOfBirth dateOfBirth;
    @Field("links")
    private Links links;
    @Field("name")
    private String name;
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

    public DateOfBirth getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
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
