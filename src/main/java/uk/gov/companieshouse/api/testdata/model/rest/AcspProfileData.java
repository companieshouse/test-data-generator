package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspProfileData {

    @JsonProperty("acsp_number")
    private final String acspNumber;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("profile_uri")
    private final String profileUri;

    public AcspProfileData(String acspNumber, String name, String profileUri) {
        this.acspNumber = acspNumber;
        this.name = name;
        this.profileUri = profileUri;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getName() {
        return name;
    }

    public String getProfileUri() {
        return profileUri;
    }
}
