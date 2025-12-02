package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SubcategoryType {
    ACQUIRE("acquire"),
    ADMINISTRATION("administration"),
    ALTER("alter"),
    ANNUAL_RETURN("annual-return"),
    APPOINTMENTS("appointments"),
    CERTIFICATE("certificate"),
    CHANGE("change"),
    COMPULSORY("compulsory"),
    COURT_ORDER("court-order"),
    CREATE("create"),
    DEBENTURE("debenture"),
    DOCUMENT_REPLACEMENT("document-replacement"),
    INVESTMENT_COMPANY("investment-company"),
    MORTGAGE("mortgage"),
    NOTIFICATIONS("notifications"),
    OFFICERS("officers"),
    OTHER("other"),
    RECEIVER("receiver"),
    REGISTER("register"),
    RELEASE_CEASE("release-cease"),
    RESOLUTION("resolution"),
    SATISFY("satisfy"),
    SOCIAL_LANDLORD("social-landlord"),
    STATEMENTS("statements"),
    TERMINATION("termination"),
    TRANSFER("transfer"),
    TRUSTEE("trustee"),
    VOLUNTARY("voluntary"),
    VOLUNTARY_ARRANGEMENT("voluntary-arrangement"),
    VOLUNTARY_ARRANGEMENT_MORATORIA("voluntary-arrangement-moratoria");

    private final String value;

    SubcategoryType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
