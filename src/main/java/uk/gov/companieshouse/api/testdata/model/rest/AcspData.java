package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspData {

    @JsonProperty("acsp_number")
    private long acspNumber;


    public AcspData(long acspNumber) {
        this.acspNumber = acspNumber;
    }

    public long getAcspNumber() {
        return acspNumber;
    }

}
