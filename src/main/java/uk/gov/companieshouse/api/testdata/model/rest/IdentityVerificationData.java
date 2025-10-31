package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityVerificationData {

    @JsonProperty("identity_id")
    private final String identityId;

    @JsonProperty("uvid")
    private final String uvid;

    public IdentityVerificationData (String identityId, String uvid){
        this.identityId = identityId;
        this.uvid = uvid;
    }

    public String getIdentityId() {
        return identityId;
    }

    public String getUvid() {
        return uvid;
    }
}
