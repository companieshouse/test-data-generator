package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class ResolutionsSpec {

    @JsonProperty("category")
    @Pattern(regexp = "auditors|capital|change-of-name|incorporation|insolvency|liquidation|miscellaneous|other|resolution", message = "Invalid resolution category")
    private String category;

    @JsonProperty("description")
    private ResolutionDescriptionType description;

    @JsonProperty("subcategory")
    @Pattern(regexp = "resolution|voluntary", message = "Invalid resolution subcategory")
    private String subCategory;

    @JsonProperty("type")
    private ResolutionType type;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ResolutionDescriptionType getDescription() {
        return description;
    }

    public void setDescription(ResolutionDescriptionType description) {
        this.description = description;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public ResolutionType getType() {
        return type;
    }

    public void setType(ResolutionType type) {
        this.type = type;
    }
}
