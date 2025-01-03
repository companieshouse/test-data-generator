package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("forename")
    private final String forename;

    @JsonProperty("surname")
    private final String surname;

    public UserData(String userId, String email, String forename, String surname) {
        this.userId = userId;
        this.email = email;
        this.forename = forename;
        this.surname = surname;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }
}
