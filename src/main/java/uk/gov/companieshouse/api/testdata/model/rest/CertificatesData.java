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

    @JsonProperty("description")
    private final String description;

    @JsonProperty("description_identifier")
    private final String descriptionIdentifier;

    @JsonProperty("description_company_number")
    private final String descriptionCompanyNumber;

    @JsonProperty("description_certificate")
    private final String descriptionCertificate;

    @JsonProperty("item_options_certificate_type")
    private final String itemOptionsCertificateType;

    @JsonProperty("item_options_company_Type")
    private final String itemOptionsCompanyType;

    @JsonProperty("item_options_company_status")
    private final String itemOptionsCompanyStatus;

    @JsonProperty("etag")
    private final String etag;

    @JsonProperty("kind")
    private final String kind;

    @JsonProperty("links_self")
    private final String linksSelf;

    @JsonProperty("postal_delivery")
    private final boolean postalDelivery;

    @JsonProperty("quantity")
    private final int quantity;

    @JsonProperty("user_id")
    private final String userId;

    public CertificatesData(String id, String createdAt, String updatedAt, String dataId, String companyName,
                            String companyNumber, String description, String descriptionIdentifier,
                            String descriptionCompanyNumber, String descriptionCertificate, String itemOptionsCertificateType,
                            String itemOptionsCompanyType, String itemOptionsCompanyStatus, String etag,
                            String kind, String linksSelf, boolean postalDelivery, int quantity, String userId) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.dataId = dataId;
        this.companyName = companyName;
        this.companyNumber = companyNumber;
        this.description = description;
        this.descriptionIdentifier = descriptionIdentifier;
        this.descriptionCompanyNumber = descriptionCompanyNumber;
        this.descriptionCertificate = descriptionCertificate;
        this.itemOptionsCertificateType = itemOptionsCertificateType;
        this.itemOptionsCompanyType = itemOptionsCompanyType;
        this.itemOptionsCompanyStatus = itemOptionsCompanyStatus;
        this.etag = etag;
        this.kind = kind;
        this.linksSelf = linksSelf;
        this.postalDelivery = postalDelivery;
        this.quantity = quantity;
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

    public String getDescription() {
        return description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public String getDescriptionCompanyNumber() {
        return descriptionCompanyNumber;
    }

    public String getDescriptionCertificate() {
        return descriptionCertificate;
    }

    public String getItemOptionsCertificateType() {
        return itemOptionsCertificateType;
    }

    public String getItemOptionsCompanyType() {
        return itemOptionsCompanyType;
    }

    public String getItemOptionsCompanyStatus() {
        return itemOptionsCompanyStatus;
    }

    public String getEtag() {
        return etag;
    }

    public String getKind() {
        return kind;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public boolean isPostalDelivery() {
        return postalDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUserId() {
        return userId;
    }
}
