package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.util.List;

public class FilingHistorySpec {
    @JsonProperty("type")
    private String type;

    @JsonProperty("subcategory")
    private SubcategoryType subCategory;

    @JsonProperty("category")
    private CategoryType category;

    @JsonProperty("description")
    private FilingHistoryDescriptionType description;

    @Size(max = 20, message = "Resolutions must not exceed 20")
    @Valid
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
        if (type != null && !AllowedFilingTypes.ALL.contains(type.trim().toUpperCase())) {
            throw new IllegalArgumentException("Invalid filing type: " + type);
        }
        this.type = type;
    }

    public CategoryType getCategory() {
        return category;
    }

    public void setCategory(CategoryType category) {
        this.category = category;
    }

    public SubcategoryType getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubcategoryType subCategory) {
        this.subCategory = subCategory;
    }

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

    public FilingHistoryDescriptionType getDescription() {
        return description;
    }

    public void setDescription(FilingHistoryDescriptionType description) {
        this.description = description;
    }
}
