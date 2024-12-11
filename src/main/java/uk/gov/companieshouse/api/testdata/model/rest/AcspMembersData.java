package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspMembersData {

    @JsonProperty("acsp_number")
    private final String acspNumber;

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("user_role")
    private final String userRole;

    @JsonProperty("status")
    private final String status;

    public AcspMembersData(String acspNumber, String userId, String userRole, String status) {
        this.acspNumber = acspNumber;
        this.userId = userId;
        this.userRole = userRole;
        this.status = status;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getStatus() {
        return status;
    }
}
