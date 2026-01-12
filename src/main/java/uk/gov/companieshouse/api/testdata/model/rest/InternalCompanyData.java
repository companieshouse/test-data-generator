package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalCompanyData {

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("auth_code")
    private final String authCode;

    @JsonProperty("company_uri")
    private final String companyUri;

    @JsonProperty("delta_at")
    private final String deltaAt;

    public InternalCompanyData(String companyNumber, String authCode, String companyUri, String deltaAt) {
        this.companyNumber = companyNumber;
        this.authCode = authCode;
        this.companyUri = companyUri;
        this.deltaAt = deltaAt;
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

    public String getDeltaAt() {
        return deltaAt;
    }
}
