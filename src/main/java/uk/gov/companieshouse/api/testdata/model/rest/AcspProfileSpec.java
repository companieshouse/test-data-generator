package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AcspProfileSpec {
    @JsonProperty
    private String status;

    @JsonProperty
    private String type;

    @JsonProperty("aml_details")
    private List<AmlSpec> amlDetails;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AmlSpec> getAmlDetails() {
        return amlDetails;
    }

    public void setAmlDetails(List<AmlSpec> amlDetails) {
        this.amlDetails = amlDetails;
    }
}
