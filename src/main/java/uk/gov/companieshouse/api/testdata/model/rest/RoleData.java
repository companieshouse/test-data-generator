package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RoleData {

    @JsonProperty("role_id")
    private final String roleId;

    public RoleData(String roleId) {
        this.roleId = roleId;
    }

    public String getRoleId() {
        return roleId;
    }
}
