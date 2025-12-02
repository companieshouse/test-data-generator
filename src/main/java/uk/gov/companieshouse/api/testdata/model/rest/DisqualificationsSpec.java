package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class DisqualificationsSpec {

    @Pattern(regexp = "court-order|undertaking|sanction", message = "Invalid disqualification type")
    @JsonProperty("disqualification_type")
    private String disqualificationType;

    @JsonProperty("is_corporate_officer")
    private Boolean isCorporateOfficer;

    public String getDisqualificationType() {
        return disqualificationType;
    }

    public void setDisqualificationType(String disqualificationType) {
        this.disqualificationType = disqualificationType;
    }

    public Boolean getCorporateOfficer() {
        return isCorporateOfficer;
    }

    public void setCorporateOfficer(Boolean corporateOfficer) {
        isCorporateOfficer = corporateOfficer;
    }

}