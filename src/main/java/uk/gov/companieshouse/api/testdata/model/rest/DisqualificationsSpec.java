package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DisqualificationsSpec {

    @JsonProperty("disqualification_type")
    private String disqualificationType;

    @JsonProperty("is_corporate_officer")
    public Boolean isCorporateOfficer;

    public String getDisqualificationType() {
        return disqualificationType;
    }

    public void setDisqualificationType(String disqualificationType) {
        this.disqualificationType = disqualificationType;
    }

    public Boolean isCorporateOfficer() {
        return isCorporateOfficer;
    }

    public void setIsCorporateOfficer(Boolean isCorporateOfficer) {
        this.isCorporateOfficer = isCorporateOfficer;
    }

}