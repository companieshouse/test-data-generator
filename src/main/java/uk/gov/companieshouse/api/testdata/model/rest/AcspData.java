package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspData {

    @JsonProperty("acsp_number")
    private final String acspNumber;

//    @JsonProperty("acsp_uri")
//    private final String acspUri;

    public AcspData(long acspNumber, String acspUri) {
        this.acspNumber = String.valueOf(acspNumber);
        //this.acspUri = acspUri;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

//    public String getAcspUri() {
//        return acspUri;
//    }

}
