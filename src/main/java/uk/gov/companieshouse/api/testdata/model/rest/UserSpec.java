package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class UserSpec {

    @JsonProperty
    private List<RoleSpec> roles;

    @JsonProperty
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "email is not a valid email address")
    private String email;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
