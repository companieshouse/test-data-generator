package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyData {

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("auth_code")
    private final String authCode;

    @JsonProperty("company_uri")
    private final String companyUri;

    @JsonProperty("company_next_accounts_start")
    private final String companyNextAccountsStart;

    @JsonProperty("company_next_accounts_end")
    private final String companyNextAccountsEnd;

    @JsonProperty("company_last_accounts_start")
    private final String companyLastAccountsStart;

    @JsonProperty("company_last_accounts_end")
    private final String companyLastAccountsEnd;

    public CompanyData(String companyNumber, String authCode, String companyUri, String companyNextAccountsStart, String companyNextAccountsEnd, String companyLastAccountsStart, String companyLastAccountsEnd) {
        this.companyNumber = companyNumber;
        this.authCode = authCode;
        this.companyUri = companyUri;
        this.companyNextAccountsStart = companyNextAccountsStart;
        this.companyNextAccountsEnd = companyNextAccountsEnd;
        this.companyLastAccountsStart = companyLastAccountsStart;
        this.companyLastAccountsEnd = companyLastAccountsEnd;
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

    public String getCompanyNextAccountsStart() {
        return companyNextAccountsStart;
    }

    public String getCompanyNextAccountsEnd() {
        return companyNextAccountsEnd;
    }

    public String getCompanyLastAccountsStart() {
        return companyLastAccountsStart;
    }

    public String getCompanyLastAccountsEnd() {
        return companyLastAccountsEnd;
    }
}
