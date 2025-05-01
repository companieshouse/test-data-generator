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

    @JsonProperty("item_options")
    private ItemOptionsSpec itemOptions;

    @JsonProperty("kind")
    private String kind;

    @JsonProperty("postal_delivery")
    private boolean postalDelivery;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("user_id")
    @NotNull(message = "User ID cannot be null")
    private String userId;

    @JsonProperty("basket")
    private BasketSpec basketSpec;

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

    public ItemOptionsSpec getItemOptions() {
        return  itemOptions;
    }

    public void setItemOptions(ItemOptionsSpec itemOptions) {
        this.itemOptions = itemOptions;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
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

    public BasketSpec getBasketSpec() {
        return basketSpec;
    }

    public void setBasketSpec(BasketSpec basketSpec) {
        this.basketSpec = basketSpec;
    }
}
