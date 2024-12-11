package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Specification for ACSP Members
 */
public class AcspMembersSpec {

    @JsonProperty
    @NotNull(message = "ACSP number cannot be null")
    private String acspNumber;

    @JsonProperty
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @JsonProperty
    private String userRole;

    @JsonIgnore
    private String status;

    public AcspMembersSpec() {
        this.userRole = "owner";
        this.status = "active";
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
