package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class DisqualificationsSpec {

    @JsonProperty("disqualification_type")
    @NotNull(message = "Disqualification type is required")
    private String disqualificationType;

    @JsonProperty("is_corporate_officer")
    @NotNull(message = "Corporate officer status is required")
    private Boolean isCorporateOfficer;

    @JsonProperty("company_number")
    @NotNull(message = "Company number is required")
    private String companyNumber;

    public String getDisqualificationType() {
        return disqualificationType;
    }

    public void setDisqualificationType(String disqualificationType) {
        this.disqualificationType = disqualificationType;
    }

    public Boolean getIsCorporateOfficer() {
        return isCorporateOfficer;
    }

    public void setIsCorporateOfficer(Boolean isCorporateOfficer) {
        this.isCorporateOfficer = isCorporateOfficer;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}