package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public class UserSpec {

    @JsonProperty("roles")
    private List<String> roles;

    @JsonProperty
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "email is not a valid email address")
    private String email;

    @JsonProperty
    @NotEmpty(message = "password is required")
    private String password;

    @JsonProperty("is_company_auth_allow_list")
    private Boolean isCompanyAuthAllowList;

    @JsonProperty("is_admin")
    private Boolean isAdmin;

    @JsonProperty("identity_verification")
    @Size(min = 1, message = "identity_verification list, if provided, cannot be empty")
    @Valid
    private List<IdentityVerificationSpec> identityVerification;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
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

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public List<IdentityVerificationSpec> getIdentityVerification() {
        return identityVerification;
    }

    public void setIdentityVerification(List<IdentityVerificationSpec> identityVerifications) {
        this.identityVerification = identityVerifications;
    }
}