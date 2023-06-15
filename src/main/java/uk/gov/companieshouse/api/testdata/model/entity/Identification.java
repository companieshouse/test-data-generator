package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Identification {

    @Field("country_registered")
    private String countryRegistered;
    @Field("legal_authority")
    private String legalAuthority;
    @Field("legal_form")
    private String legalForm;
    @Field("place_registered")
    private String placeRegistered;
    @Field("registration_number")
    private String registrationNumber;

    public String getCountryRegistered() {
        return countryRegistered;
    }

    public void setCountryRegistered(String countryRegistered) {
        this.countryRegistered = countryRegistered;
    }

    public String getLegalAuthority() {
        return legalAuthority;
    }

    public void setLegalAuthority(String legalAuthority) {
        this.legalAuthority = legalAuthority;
    }

    public String getLegalForm() {
        return legalForm;
    }

    public void setLegalForm(String legalForm) {
        this.legalForm = legalForm;
    }

    public String getPlaceRegistered() {
        return placeRegistered;
    }

    public void setPlaceRegistered(String placeRegistered) {
        this.placeRegistered = placeRegistered;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
}
