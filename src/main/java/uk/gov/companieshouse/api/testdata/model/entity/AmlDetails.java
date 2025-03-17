package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class AmlDetails {

    @Field("supervisory_body")
    private String supervisoryBody;

    @Field("membership_details")
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

