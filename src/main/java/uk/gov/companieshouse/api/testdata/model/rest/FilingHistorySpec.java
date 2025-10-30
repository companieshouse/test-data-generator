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

    @JsonProperty("resolutions")
    private List<ResolutionsSpec> resolutions;

    @JsonProperty("description_values")
    private DescriptionValuesSpec descriptionValues;

    @JsonProperty("document_metadata")
    private Boolean documentMetadata;

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

    public Boolean getDocumentMetadata() {
        return documentMetadata;
    }

    public void setDocumentMetadata(Boolean documentMetadata) {
        this.documentMetadata = documentMetadata;
    }
}
