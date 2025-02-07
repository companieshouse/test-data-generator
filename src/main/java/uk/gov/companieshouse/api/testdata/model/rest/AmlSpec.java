package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AmlSpec {
    @JsonProperty("supervisory_body")
    private String supervisoryBody;

    @JsonProperty("membership_details")
    private String membershipDetails;

    public String getSupervisoryBody() {
        return supervisoryBody;
    }

    public void setSupervisoryBody(String supervisoryBody) {
        this.supervisoryBody = supervisoryBody;
    }

    public String getMembershipDetails() {
        return membershipDetails;
    }

    public void setMembershipDetails(String membershipDetails) {
        this.membershipDetails = membershipDetails;
    }
}