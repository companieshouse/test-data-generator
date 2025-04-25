package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PscType {
    INDIVIDUAL("individual-person-with-significant-control",
            "individual", "individual"),
    LEGAL_PERSON("legal-person-person-with-significant-control",
            "legal-person", "legal"),
    CORPORATE_ENTITY("corporate-entity-person-with-significant-control",
            "corporate-entity", "corporate"),
    INDIVIDUAL_BENEFICIAL_OWNER("individual-beneficial-owner",
            "individual-beneficial-owner", "individual-bo"),
    CORPORATE_BENEFICIAL_OWNER("corporate-entity-beneficial-owner",
            "corporate-entity-beneficial-owner", "corporate-bo"),
    SUPER_SECURE_BENEFICIAL_OWNER("super-secure-beneficial-owner",
            "super-secure-beneficial-owner", "super-secure-bo"),
    SUPER_SECURE_PSC("super-secure-person-with-significant-control",
            "super-secure", "super-secure-psc");

    private final String kind;
    private final String linkType;
    private final String jsonValue;

    PscType(String kind, String linkType, String jsonValue) {
        this.kind = kind;
        this.linkType = linkType;
        this.jsonValue = jsonValue;
    }

    public String getKind() {
        return kind;
    }

    public String getLinkType() {
        return linkType;
    }

    @JsonValue
    public String getJsonValue() {
        return jsonValue;
    }

    @JsonCreator
    public static PscType fromString(String value) {
        for (PscType type : PscType.values()) {
            if (type.jsonValue.equalsIgnoreCase(value)
                    || type.name().equalsIgnoreCase(value)
                    || type.linkType.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PscType: " + value);
    }
}