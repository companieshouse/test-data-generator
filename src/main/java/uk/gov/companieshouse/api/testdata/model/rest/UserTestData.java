package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserTestData {

    @JsonProperty("user_id")
    private final String userId;
    @JsonProperty("email")
    private final String email;
    @JsonProperty("forename")
    private final String foreName;
    @JsonProperty("surname")
    private final String surName;

    public UserTestData(String userId, String email, String foreName, String surName) {
        this.userId = userId;
        this.email = email;
        this.foreName = foreName;
        this.surName = surName;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getForeName() {
        return foreName;
    }

    public String getSurName() {
        return surName;
    }
}
