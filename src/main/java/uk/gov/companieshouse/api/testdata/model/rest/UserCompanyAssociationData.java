package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.bson.types.ObjectId;
import uk.gov.companieshouse.api.testdata.model.entity.Invitation;
import uk.gov.companieshouse.api.testdata.model.entity.PreviousState;

public class UserCompanyAssociationData {
    @JsonProperty("id")
    private final String id;

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("user_email")
    private final String userEmail;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("approval_route")
    private final String approvalRoute;

    @JsonProperty("invitations")
    private final List<Invitation> invitations;

    @JsonProperty("previous_states")
    private final List<PreviousState> previousStates;

    public UserCompanyAssociationData(ObjectId id,
                                      String companyNumber, String userId,
                                      String userEmail, String status,
                                      String approvalRoute, List<Invitation> invitations,
                                      List<PreviousState> previousStates) {
        this.id = id.toString();
        this.companyNumber = companyNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
        this.approvalRoute = approvalRoute;
        this.invitations = invitations;
        this.previousStates = previousStates;
    }

    public String getId() {
        return id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getStatus() {
        return status;
    }

    public String getApprovalRoute() {
        return approvalRoute;
    }

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public List<PreviousState> getPreviousStates() {
        return previousStates;
    }
}