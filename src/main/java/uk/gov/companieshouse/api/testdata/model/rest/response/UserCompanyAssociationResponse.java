package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import org.bson.types.ObjectId;
import uk.gov.companieshouse.api.testdata.model.entity.Invitation;

public class UserCompanyAssociationResponse {
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

    public UserCompanyAssociationResponse(ObjectId id,
                                          String companyNumber, String userId,
                                          String userEmail, String status,
                                          String approvalRoute, List<Invitation> invitations) {
        this.id = id.toString();
        this.companyNumber = companyNumber;
        this.userId = userId;
        this.userEmail = userEmail;
        this.status = status;
        this.approvalRoute = approvalRoute;
        this.invitations = invitations;
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
}