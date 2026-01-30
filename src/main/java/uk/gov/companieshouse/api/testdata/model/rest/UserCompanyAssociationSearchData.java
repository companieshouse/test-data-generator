package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.List;

public class UserCompanyAssociationSearchData {
    private String id;
    private String companyNumber;
    private String userId;
    private String approvalRoute;
    private List<String> invitations;
    private String status;
    private String associationLink;

    public UserCompanyAssociationSearchData() {
    }

    public UserCompanyAssociationSearchData(
            String id, String companyNumber, String userId, String status,
            String approvalRoute, List<String> invitations, String associationLink) {
        this.id = id;
        this.companyNumber = companyNumber;
        this.userId = userId;
        this.approvalRoute = approvalRoute;
        this.invitations = invitations;
        this.status = status;
        this.associationLink = associationLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void setApprovalRoute(String approvalRoute) {
        this.approvalRoute = approvalRoute;}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setInvitations(List<String> invitations) {
        this.invitations = invitations;
    }

    public void setAssociationLink(String associationLink) {
        this.associationLink = associationLink;
    }
}
