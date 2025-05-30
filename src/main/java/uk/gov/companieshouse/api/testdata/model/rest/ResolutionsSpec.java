package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResolutionsSpec {

    @JsonProperty("barcode")
    private String barcode;

    @JsonProperty("category")
    private String category;

    @JsonProperty("delta_at")
    private String deltaAt;

    @JsonProperty("description")
    private String description;

    @JsonProperty("subcategory")
    private String subCategory;

    @JsonProperty("type")
    private String type;

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(String deltaAt) {
        this.deltaAt = deltaAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
