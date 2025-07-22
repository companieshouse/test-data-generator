package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "missing_image_deliveries")
public class MissingImageDeliveries {
    @Id
    @Field("_id")
    private String id;

    @Field("created_at")
    private String createdAt;

    @Field("updated_at")
    private String updatedAt;

    @Field("data.id")
    private String dataId;

    @Field("data.company_name")
    private String companyName;

    @Field("data.company_number")
    private String companyNumber;

    @Field("data.description")
    private String description;

    @Field("data.description_identifier")
    private String descriptionIdentifier;

    @Field("data.description_values.company_number")
    private String descriptionCompanyNumber;

    @Field("data.description_values.missing-image-delivery")
    private String descriptionMissingImageDelivery;

    @Field("data.etag")
    private String etag;

    @Field("data.item_costs")
    private List<ItemCosts> itemCosts;

    @Field("data.item_options")
    private ItemOptions itemOptions;

    @Field("data.kind")
    private String kind;

    @Field("data.links.self")
    private String linksSelf;

    @Field("data.postage_cost")
    private String postageCost;

    @Field("data.postal_delivery")
    private boolean postalDelivery;

    @Field("data.quantity")
    private int quantity;

    @Field("data.total_item_cost")
    private String totalItemCost;

    @Field("user_id")
    private String userId;

    @Field("basket")
    private Basket basket;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

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

    public String getDescriptionMissingImageDelivery() { return descriptionMissingImageDelivery; }

    public void setDescriptionMissingImageDelivery(String descriptionMissingImageDelivery) {
        this.descriptionMissingImageDelivery = descriptionMissingImageDelivery;
    }

    public List<ItemCosts> getItemCosts() { return itemCosts; }

    public void setItemCosts(List<ItemCosts> itemCosts) {
        this.itemCosts = itemCosts;
    }

    public ItemOptions getItemOptions() {
        return itemOptions;
    }

    public void setItemOptions(ItemOptions itemOptions) {
        this.itemOptions = itemOptions;
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

    public String getPostageCost() { return postageCost; }

    public void setPostageCost(String postageCost) {
        this.postageCost = postageCost;
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

    public String getTotalItemCost() { return  totalItemCost; }

    public void setTotalItemCost(String totalItemCost) {
        this.totalItemCost = totalItemCost;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Basket getBasket() {
        return basket;
    }

    public void setBasket(Basket basket) {
        this.basket = basket;
    }
}
