package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.List;

public class UserCompanyAssociationRequest {
    @JsonProperty("company_number")
    @NotNull(message = "Company number is required")
    private String companyNumber;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty("user_email")
    private String userEmail;

    @JsonProperty("status")
    @Pattern(regexp = "confirmed|removed|migrated|awaiting-approval|unauthorised",
            message = "Invalid association status")
    private String status;

    @JsonProperty("approval_route")
    @Pattern(regexp = "auth_code|invitation|migration",
            message = "Invalid approval route")
    private String approvalRoute;

    @JsonProperty("invitations")
    private List<InvitationRequest> invitations;

    @JsonProperty("approval_expiry_at")
    private Instant approvalExpiryAt;

    @JsonProperty("previous_states")
    private List<PreviousStateSpec> previousStates;

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalRoute() {
        return approvalRoute;
    }

    public void setApprovalRoute(String approvalRoute) {
        this.approvalRoute = approvalRoute;
    }

    public List<InvitationRequest> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<InvitationRequest> invitations) {
        this.invitations = invitations;
    }

    public Instant getApprovalExpiryAt() {
        return approvalExpiryAt;
    }

    public void setApprovalExpiryAt(Instant approvalExpiryAt) {
        this.approvalExpiryAt = approvalExpiryAt;
    }

    public List<PreviousStateSpec> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(List<PreviousStateSpec> previousStates) {
        this.previousStates = previousStates;
    }
}