package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class AccountPenaltyRequest {

    @JsonProperty("company_code")
    @NotBlank(message = "company code should not be blank")
    private String companyCode;

    @JsonProperty("customer_code")
    @NotBlank(message = "customer code should not be blank")
    private String customerCode;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }
}
