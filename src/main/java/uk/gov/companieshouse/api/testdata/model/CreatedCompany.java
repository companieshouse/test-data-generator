package uk.gov.companieshouse.api.testdata.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreatedCompany {

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("auth_code")
    private final String authCode;

    public CreatedCompany(String companyNumber, String authCode) {
        this.companyNumber = companyNumber;
        this.authCode = authCode;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getAuthCode() {
        return authCode;
    }
}
