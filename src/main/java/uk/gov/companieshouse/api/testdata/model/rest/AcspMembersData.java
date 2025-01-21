package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspMembersData {

    @JsonProperty("acsp_member_id")
    private final String acspMemberId;

    @JsonProperty("acsp_number")
    private final String acspNumber;

    @JsonProperty("user_id")
    private final String userId;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("user_role")
    private final String userRole;

    public AcspMembersData(String acspMemberId,
                           String acspNumber,
                           String userId,
                           String status,
                           String userRole) {
        this.acspMemberId = acspMemberId;
        this.acspNumber = acspNumber;
        this.userId = userId;
        this.status = status;
        this.userRole = userRole;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getAcspMemberId() {
        return acspMemberId;
    }

    public String getUserRole() {
        return userRole;
    }

    public String getStatus() {
        return status;
    }
}
