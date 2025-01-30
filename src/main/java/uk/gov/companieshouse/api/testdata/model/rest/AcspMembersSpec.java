package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class AcspMembersSpec {
    @JsonProperty
    private String acspNumber;

    @JsonProperty("user_id")
    @NotNull(message = "User not present")
    private String userId;

    @JsonProperty("status")
    @Pattern(regexp = "active|suspended|ceased", message = "Invalid acsp member status")
    private String status;

    @JsonProperty("user_role")
    @Pattern(regexp = "owner|admin|standard", message = "Invalid user role")
    private String userRole;

    @JsonProperty("acsp_profile")
    AcspProfileSpec acspProfile;

    public AcspProfileSpec getAcspProfile() {
        return acspProfile;
    }

    public void setAcspProfile(AcspProfileSpec acspProfile) {
        this.acspProfile = acspProfile;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
