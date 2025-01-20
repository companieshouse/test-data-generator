package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityData {
    @JsonProperty("id")
    private final String id;

    public IdentityData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
