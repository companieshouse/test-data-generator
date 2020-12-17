package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyData {

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("auth_code")
    private final String authCode;

    @JsonProperty("company_uri")
    private final String companyUri;

    public CompanyData(String companyNumber, String authCode, String companyUri) {
        this.companyNumber = companyNumber;
        this.authCode = authCode;
        this.companyUri = companyUri;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getCompanyUri() {
        return companyUri;
    }
}
