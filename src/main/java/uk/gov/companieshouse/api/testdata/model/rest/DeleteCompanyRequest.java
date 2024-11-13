package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Body for a delete company request
 *
 */
public class DeleteCompanyRequest {

    @JsonProperty("auth_code")
    @NotNull(message="company auth_code required")
    private String authCode;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }
}
