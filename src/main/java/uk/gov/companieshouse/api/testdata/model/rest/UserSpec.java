package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UserSpec {

    @JsonProperty("roles")
    private List<RoleSpec> roles;

    @JsonProperty
    @NotNull(message = "password is required")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RoleSpec> getRoles() {
        return roles;
    }

    public void setRoles(List<RoleSpec> roles) {
        this.roles = roles;

    }
}