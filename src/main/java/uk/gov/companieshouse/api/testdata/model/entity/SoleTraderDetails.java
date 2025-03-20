package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class SoleTraderDetails {

    @Field("forename")
    private String forename;

    @Field("surname")
    private String surname;

    @Field("nationality")
    private String nationality;

    @Field("usual_residential_country")
    private String usualResidentialCountry;

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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getUsualResidentialCountry() {
        return usualResidentialCountry;
    }

    public void setUsualResidentialCountry(String usualResidentialCountry) {
        this.usualResidentialCountry = usualResidentialCountry;
    }
}