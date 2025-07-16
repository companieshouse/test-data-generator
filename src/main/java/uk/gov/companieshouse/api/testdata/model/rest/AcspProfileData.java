package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspProfileData {

    @JsonProperty("acsp_number")
    private final String acspNumber;

     @JsonProperty("name")
    private final String name;

    public AcspProfileData(String acspNumber, String name) {
        this.acspNumber = acspNumber;
        this.name = name;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getName() {
        return name;
    }
}
