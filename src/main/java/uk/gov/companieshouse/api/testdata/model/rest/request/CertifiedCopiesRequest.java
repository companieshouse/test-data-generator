package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CertifiedCopiesRequest {

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("company_number")
    private String companyNumber;

    @JsonProperty("customer_reference")
    private String customerReference;

    @JsonProperty("item_costs")
    private List<ItemCostsRequest> itemCosts;

    @JsonProperty("item_options")
    private List<ItemOptionsRequest> itemOptions;

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
    private BasketRequest basketRequest;

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

    public List<ItemCostsRequest> getItemCosts() { return itemCosts; }

    public void setItemCosts(List<ItemCostsRequest> itemCosts) {
        this.itemCosts = itemCosts;
    }

    public List<ItemOptionsRequest> getItemOptions() {
        return  itemOptions;
    }

    public void setItemOptions(List<ItemOptionsRequest> itemOptions) {
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

    public BasketRequest getBasketSpec() {
        return basketRequest;
    }

    public void setBasketSpec(BasketRequest basketRequest) {
        this.basketRequest = basketRequest;
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
