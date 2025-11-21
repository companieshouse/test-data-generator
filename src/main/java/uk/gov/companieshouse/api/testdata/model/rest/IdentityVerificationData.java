package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityVerificationData {

    @JsonProperty("identity_id")
    private final String identityId;

    @JsonProperty("uvid")
    private final String uvid;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    public IdentityVerificationData(String identityId, String uvid,
                                      String firstName, String lastName) {
        this.identityId = identityId;
        this.uvid = uvid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getIdentityId() {
        return identityId;
    }

    public String getUvid() {
        return uvid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
