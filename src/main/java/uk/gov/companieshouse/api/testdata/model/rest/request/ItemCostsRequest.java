package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemCostsRequest {
    @JsonProperty("discount_applied")
    private String discountApplied;

    @JsonProperty("item_cost")
    private String itemCost;

    @JsonProperty("calculated_cost")
    private String calculatedCost;

    @JsonProperty("product_type")
    private String productType;

    public String getDiscountApplied() { return discountApplied; }

    public void setDiscountApplied(String discountApplied) {
        this.discountApplied = discountApplied;
    }

    public String getItemCost() { return itemCost; }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }

    public String getCalculatedCost() { return calculatedCost; }

    public void setCalculatedCost(String calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public String getProductType() { return productType; }

    public void setProductType(String productType) {
        this.productType = productType;
    }

}
