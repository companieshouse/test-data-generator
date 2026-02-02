package uk.gov.companieshouse.api.testdata.model.rest;

public class UserCompanyAssociationSearchData {
    private String id;
    private String companyNumber;
    private String userId;
    private String status;

    public UserCompanyAssociationSearchData(
            String id, String companyNumber, String userId, String status) {
        this.id = id;
        this.companyNumber = companyNumber;
        this.userId = userId;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
