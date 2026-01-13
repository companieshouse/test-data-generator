package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCompanyAssociationData {

    private String id;
    private String companyNumber;
    private String userId;
    private String userEmail;
    private String status;
    private String approvalRoute;
    private Object invitations;

    @JsonProperty("association_link")
    private String associationLink;

    // Simple constructor for SDK response
    public UserCompanyAssociationData() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this.associationLink = "/associations/" + id;
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

    public Object getInvitations() {
        return invitations;
    }

    public void setInvitations(Object invitations) {
        this.invitations = invitations;
    }

    public String getAssociationLink() {
        return associationLink;
    }

    public void setAssociationLink(String associationLink) {
        this.associationLink = associationLink;
    }
}