package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class UsersSpec {

    @JsonProperty("roles")
    private List<RolesSpec> roles;

    @JsonProperty
    @NotNull(message = "password is required")
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<RolesSpec> getRoles() {
        return roles;
    }

    public void setRoles(List<RolesSpec> roles) {
        this.roles = roles;

    }
}
