package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Jurisdiction {
    @JsonProperty("england/wales")
    ENGLAND_WALES, 
    @JsonProperty("scotland")
    SCOTLAND, 
    @JsonProperty("northern-ireland")
    NI;
}
