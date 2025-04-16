package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CertificatesData {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    @JsonProperty("data_id")
    private final String dataId;

    @JsonProperty("company_name")
    private final String companyName;

    @JsonProperty("company_number")
    private final String companyNumber;

    @JsonProperty("user_id")
    private final String userId;

    public CertificatesData(String id, String createdAt, String updatedAt, String dataId, String companyName,
                            String companyNumber, String userId) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.dataId = dataId;
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.userId = userId;
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

    public String getDataId() {
        return dataId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public String getUserId() {
        return userId;
    }
}
