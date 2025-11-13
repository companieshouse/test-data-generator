package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {
    ACCOUNTS("accounts"),
    ADDRESS("address"),
    ANNOTATION("annotation"),
    ANNUAL_RETURN("annual-return"),
    AUDITORS("auditors"),
    CAPITAL("capital"),
    CERTIFICATE("certificate"),
    CHANGE_OF_CONSTITUTION("change-of-constitution"),
    CHANGE_OF_NAME("change-of-name"),
    CONFIRMATION_STATEMENT("confirmation-statement"),
    COURT_ORDER("court-order"),
    DISSOLUTION("dissolution"),
    DOCUMENT_REPLACEMENT("document-replacement"),
    GAZETTE("gazette"),
    HISTORICAL("historical"),
    INCORPORATION("incorporation"),
    INSOLVENCY("insolvency"),
    LIQUIDATION("liquidation"),
    MISCELLANEOUS("miscellaneous"),
    MORTGAGE("mortgage"),
    OFFICER("officer"),
    OFFICERS("officers"),
    OTHER("other"),
    PERSONS_WITH_SIGNIFICANT_CONTROL("persons-with-significant-control"),
    REREGISTRATION("reregistration"),
    RESOLUTION("resolution"),
    RESTORATION("restoration"),
    RETURN("return"),
    SOCIAL_LANDLORD("social-landlord");

    private final String value;

    CategoryType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
