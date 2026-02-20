package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class DeleteAppealsRequest {
    @JsonProperty("penalty_reference")
    @NotBlank (message = "penalty reference should not be blank")
    private String penaltyReference;

    @JsonProperty("company_number")
    @NotBlank (message = "company number should not be blank")
    private String companyNumber;

    public String getPenaltyReference() {
        return penaltyReference;
    }

    public void setPenaltyReference(String penaltyReference) {
        this.penaltyReference = penaltyReference;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
