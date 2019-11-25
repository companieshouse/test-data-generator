package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Jurisdiction {

    ENGLAND_WALES("england-wales"),
    SCOTLAND("scotland"),
    NI("northern-ireland");

    @JsonValue
    private final String jurisdictionString;

    Jurisdiction(String jurisdictionString) {
        this.jurisdictionString = jurisdictionString;
    }

    @Override
    public String toString() {
        return jurisdictionString;
    }
}
