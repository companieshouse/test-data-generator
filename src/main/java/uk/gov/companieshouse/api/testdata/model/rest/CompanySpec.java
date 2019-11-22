package uk.gov.companieshouse.api.testdata.model.rest;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Requirements a new company must meet
 *
 */
public class CompanySpec {

    @JsonProperty
    @NotNull
    private Jurisdiction jurisdiction = Jurisdiction.ENGLAND_WALES;

    @JsonIgnore
    private String companyNumber;

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
}
