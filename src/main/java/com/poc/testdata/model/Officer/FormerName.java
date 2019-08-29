package com.poc.testdata.model.Officer;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

public class FormerName {

    @Field("forenames")
    private String forenames;
    @Field("surname")
    private String surname;

    public String getForenames() {
        return forenames;
    }

    public void setForenames(String forenames) {
        this.forenames = forenames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
