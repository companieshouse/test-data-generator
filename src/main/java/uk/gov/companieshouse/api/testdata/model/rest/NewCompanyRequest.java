package uk.gov.companieshouse.api.testdata.model.rest;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NewCompanyRequest {

    @JsonProperty
    @NotNull
    private Jurisdiction jurisdiction;

    public Jurisdiction getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(Jurisdiction jurisdiction) {
        this.jurisdiction = jurisdiction;
    }
}
