package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspMembersData {
    @JsonProperty("acspNumber")
    private final String acspNumber;

    @JsonProperty("userId")
    private final String userId;

    @JsonProperty("acspMemberId")
    private final String acspMemberId;

    @JsonProperty("userRole")
    private final String userRole;

    @JsonProperty("status")
    private final String status;

    public AcspMembersData(String acspNumber, String userId, String acspMemberId,
                           String userRole, String status) {
        this.acspNumber = acspNumber;
        this.userId = userId;
        this.acspMemberId = acspMemberId;
        this.userRole = userRole;
        this.status = status;
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
