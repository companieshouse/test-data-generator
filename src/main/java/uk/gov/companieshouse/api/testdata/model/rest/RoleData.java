package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleData {

    @JsonProperty("id")
    private final String id;

    public RoleData(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
