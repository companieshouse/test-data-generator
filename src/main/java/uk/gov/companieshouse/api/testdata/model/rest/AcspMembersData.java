package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspMembersData {

    @JsonProperty("acspMemberId")
    private final String acspMemberId;

    @JsonProperty("acspNumber")
    private final String acspNumber;

    @JsonProperty("userId")
    private final String userId;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("userRole")
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
