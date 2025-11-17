package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyAuthCodeData {

    @JsonProperty("id")
    private String id;

    @JsonProperty("authCode")
    private String authCode;

    public CompanyAuthCodeData(String id, String authCode) {
        this.id = id;
        this.authCode = authCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}