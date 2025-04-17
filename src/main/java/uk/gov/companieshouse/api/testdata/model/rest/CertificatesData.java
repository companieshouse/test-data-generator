package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertificatesData {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    public CertificatesData(String id, String createdAt, String updatedAt ) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}
