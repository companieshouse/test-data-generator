package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CertifiedCopiesSpec {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("item_costs")
    private List<ItemCostsSpec> itemCosts;

    @JsonProperty("item_options")
    private List<ItemOptionsSpec> itemOptions;

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

    @JsonProperty("postage_cost")
    private String postageCost;

    @JsonProperty("total_item_cost")
    private String totalItemCost;

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

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference){
        this.customerReference = customerReference;
    }

    public List<ItemCostsSpec> getItemCosts() { return itemCosts; }

    public void setItemCosts(List<ItemCostsSpec> itemCosts) {
        this.itemCosts = itemCosts;
    }

    public List<ItemOptionsSpec> getItemOptions() {
        return  itemOptions;
    }

    public void setItemOptions(List<ItemOptionsSpec> itemOptions) {
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

    public String getPostageCost() { return postageCost; }

    public void setPostageCost(String postageCost) {
        this.postageCost = postageCost;
    }

    public String getTotalItemCost() { return totalItemCost; }

    public void setTotalItemCost(String totalItemCost) {
        this.totalItemCost = totalItemCost;
    }
}
