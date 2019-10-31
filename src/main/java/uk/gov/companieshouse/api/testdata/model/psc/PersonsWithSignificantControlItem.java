package uk.gov.companieshouse.api.testdata.model.psc;

import uk.gov.companieshouse.api.testdata.model.Address;
import uk.gov.companieshouse.api.testdata.model.DateOfBirth;
import uk.gov.companieshouse.api.testdata.model.Links;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class PersonsWithSignificantControlItem {

    @Field("address")
    private Address address;
    @Field("date_of_birth")
    private DateOfBirth dateOfBirth;
    @Field("links")
    private Links links;
    @Field("name")
    private String name;
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
