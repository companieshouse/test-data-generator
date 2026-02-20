package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class IdentityVerificationResponse {

    @JsonProperty("identity_id")
    private final String identityId;

    @JsonProperty("uvid")
    private final String uvid;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    public IdentityVerificationResponse(String identityId, String uvid,
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
