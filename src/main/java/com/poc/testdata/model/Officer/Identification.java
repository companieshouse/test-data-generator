package com.poc.testdata.model.Officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class Identification {

    @Field("identification_type")
    private String identificationType;
    @Field("legal_authority")
    private String legalAuthority;
    @Field("legal_form")
    private String legalForm;
    @Field("place_registered")
    private String placeRegistered;
    @Field("registration_number")
    private String registrationNumber;

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
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
