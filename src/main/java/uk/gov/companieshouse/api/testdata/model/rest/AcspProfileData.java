package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspProfileData {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("acspNumber")
    private final String acspNumber;

    public AcspProfileData(String id, String acspNumber) {
        this.id = id;
        this.acspNumber = acspNumber;
    }

    public String getId() {
        return id;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

}
