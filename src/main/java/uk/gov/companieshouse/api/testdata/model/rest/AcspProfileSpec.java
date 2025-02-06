package uk.gov.companieshouse.api.testdata.model.rest;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.AmlDetails;

public class AcspProfileSpec {
    @JsonProperty
    private String status;

    @JsonProperty
    private String type;

    @JsonProperty
    private List<AmlDetails> amlDetails;


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

    public List<AmlDetails> getAmlDetails() {
        return amlDetails;
    }

    public void setAmlDetails(List<AmlDetails> amlDetails) {
        this.amlDetails = amlDetails;
    }
}
