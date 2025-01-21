package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspProfileData {

    @JsonProperty("acsp_number")
    private final String acspNumber;

    public AcspProfileData(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

}
