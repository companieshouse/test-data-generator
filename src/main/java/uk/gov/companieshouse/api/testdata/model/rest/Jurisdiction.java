package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Jurisdiction {

    ENGLAND_WALES("england-wales", ""),
    SCOTLAND("scotland", "SC"),
    NI("northern-ireland", "NI"),
    UNITED_KINGDOM("united-kingdom", "OE");

    @JsonValue
    private final String jurisdictionString;
    private final String companyNumberPrefix;

    Jurisdiction(String jurisdictionString, String companyNumberPrefix) {
        this.jurisdictionString = jurisdictionString;
        this.companyNumberPrefix = companyNumberPrefix;
    }

    public String getCompanyNumberPrefix() {
        return companyNumberPrefix;
    }

    @Override
    public String toString() {
        return jurisdictionString;
    }
}