package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "user_company_associations")
public class UserCompanyAssociation {
    @Id
    @Field("_id")
    private ObjectId id;

    @Field("company_number")
    private String companyNumber;

    @Field("user_id")
    private String userId;

    @Field("user_email")
    private String userEmail;

    @Field("status")
    private String status;

    @Field("approval_route")
    private String approvalRoute;

    @Field("invitations")
    private List<Invitation> invitations;

    @Field("approval_expiry_at")
    private Instant approvalExpiryAt;

    @Field("previous_states")
    private List<PreviousState> previousStates;

    @Field("created_at")
    private Instant createdAt;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

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

    public List<Invitation> getInvitations() {
        return invitations;
    }

    public void setInvitations(List<Invitation> invitations) {
        this.invitations = invitations;
    }

    public Instant getApprovalExpiryAt() {
        return approvalExpiryAt;
    }

    public void setApprovalExpiryAt(Instant approvalExpiryAt) {
        this.approvalExpiryAt = approvalExpiryAt;
    }

    public List<PreviousState> getPreviousStates() {
        return previousStates;
    }

    public void setPreviousStates(List<PreviousState> previousStates) {
        this.previousStates = previousStates;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}