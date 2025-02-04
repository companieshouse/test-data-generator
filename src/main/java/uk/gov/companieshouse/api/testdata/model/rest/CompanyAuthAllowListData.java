package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CompanyAuthAllowListData {
    @JsonProperty("id")
    private String id;

    public CompanyAuthAllowListData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
