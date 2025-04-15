package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class CertificatesSpec {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    @JsonProperty("description_company_number")
    private String descriptionCompanyNumber;

    @JsonProperty("description_certificate")
    private String descriptionCertificate;

    @JsonProperty("item_options_certificate_type")
    private String itemOptionsCertificateType;

    @JsonProperty("item_options_company_type")
    private String itemOptionsCompanyType;

    @JsonProperty("item_options_company_status")
    private String itemOptionsCompanyStatus;

    @JsonProperty("etag")
    private String etag;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("links_self")
    private String linksSelf;

    @JsonProperty("postal_delivery")
    private boolean postalDelivery;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private String userId;

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }

    public String getDescriptionCompanyNumber() {
        return descriptionCompanyNumber;
    }

    public void setDescriptionCompanyNumber(String descriptionCompanyNumber) {
        this.descriptionCompanyNumber = descriptionCompanyNumber;
    }

    public String getDescriptionCertificate() {
        return descriptionCertificate;
    }

    public void setDescriptionCertificate(String descriptionCertificate) {
        this.descriptionCertificate = descriptionCertificate;
    }

    public String getItemOptionsCertificateType() {
        return itemOptionsCertificateType;
    }

    public void setItemOptionsCertificateType(String descriptionCertificate) {
        this.descriptionCertificate = descriptionCertificate;
    }

    public String getItemOptionsCompanyType() {
        return itemOptionsCompanyType;
    }

    public void setItemOptionsCompanyType(String itemOptionsCompanyType) {
        this.itemOptionsCompanyType = itemOptionsCompanyType;
    }

    public String getItemOptionsCompanyStatus() {
        return itemOptionsCompanyStatus;
    }

    public void setItemOptionsCompanyStatus(String itemOptionsCompanyStatus) {
        this.itemOptionsCompanyStatus = itemOptionsCompanyStatus;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public void setLinksSelf(String linksSelf) {
        this.linksSelf = linksSelf;
    }

    public boolean isPostalDelivery() {
        return postalDelivery;
    }

    public void setPostalDelivery(boolean postalDelivery) {
        this.postalDelivery = postalDelivery;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
