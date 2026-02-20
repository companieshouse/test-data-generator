package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AcspProfileRequest {
    @JsonProperty
    private String status;

    @JsonProperty
    private String type;

    @JsonProperty("acsp_number")
    private String acspNumber;

    @JsonProperty
    private String name;

    @JsonProperty("aml_details")
    private List<AmlRequest> amlDetails;

    @JsonProperty("email")
    private String email;

    @JsonProperty("business_sector")
    private String businessSector;

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

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public List<AmlRequest> getAmlDetails() {
        return amlDetails;
    }

    public void setAmlDetails(List<AmlRequest> amlDetails) {
        this.amlDetails = amlDetails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusinessSector() { return businessSector; }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
