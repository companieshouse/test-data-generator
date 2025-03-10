package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class UserSpec {

    @JsonProperty
    private List<RoleSpec> roles;

    @JsonProperty
    @NotEmpty(message = "password is required")
    private String password;

    @JsonProperty("is_company_auth_allow_list")
    private Boolean isCompanyAuthAllowList;

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

    public Boolean getIsCompanyAuthAllowList() {
        return isCompanyAuthAllowList;
    }

    public void setIsCompanyAuthAllowList(Boolean isCompanyAuthAllowList) {
        this.isCompanyAuthAllowList = isCompanyAuthAllowList;
    }
}
