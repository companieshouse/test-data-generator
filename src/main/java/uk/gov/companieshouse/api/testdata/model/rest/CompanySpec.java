package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Requirements a new company must meet
 *
 */
public class CompanySpec {

    @JsonProperty
    @NotNull(message="invalid jurisdiction")
    private Jurisdiction jurisdiction;

    @JsonIgnore
    private String companyNumber;

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("type")
    private String companyType;

    public CompanySpec() {
        jurisdiction = Jurisdiction.ENGLAND_WALES;
    }

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyStatus(){return companyStatus;}

    public String getCompanyType(){return companyType;}

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }
}
