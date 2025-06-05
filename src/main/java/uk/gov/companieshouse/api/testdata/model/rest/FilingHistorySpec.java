package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class FilingHistorySpec {
    @JsonProperty("type")
    private String type;

    @JsonProperty("subcategory")
    private String subCategory;

    @JsonProperty("category")
    private String category;

    @JsonProperty("description")
    private String description;

    @JsonProperty("original_description")
    private String originalDescription;

    @JsonProperty("resolutions")
    private List<ResolutionsSpec> resolutions;

    @JsonProperty("description_values")
    private DescriptionValuesSpec descriptionValues;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public String getSubCategory() { return subCategory; }

    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }

    public List<ResolutionsSpec> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<ResolutionsSpec> resolutions) {
        this.resolutions = resolutions;
    }

    public DescriptionValuesSpec getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(DescriptionValuesSpec descriptionValues) {
        this.descriptionValues = descriptionValues;
    }
}
