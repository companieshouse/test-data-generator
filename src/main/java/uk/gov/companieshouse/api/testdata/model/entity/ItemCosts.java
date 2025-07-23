package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class ItemCosts {
    @Field("discount_applied")
    private String discountApplied;

    @Field("item_cost")
    private String itemCost;

    @Field("calculated_cost")
    private String calculatedCost;

    @Field("product_type")
    private String productType;

    public String getDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(String discountApplied) {
        this.discountApplied = discountApplied;
    }

    public String getItemCost() {
        return  itemCost;
    }

    public void setItemCost(String itemCost) {
        this.itemCost = itemCost;
    }

    public String getCalculatedCost() {
        return calculatedCost;
    }

    public void setCalculatedCost(String calculatedCost) {
        this.calculatedCost = calculatedCost;
    }

    public String getProductType() {
        return  productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
