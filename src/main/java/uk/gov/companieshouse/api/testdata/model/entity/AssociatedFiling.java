package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

public class AssociatedFiling {

    @Field("category")
    private String category;
    @Field("date")
    private Instant date;
    @Field("description")
    private String description;
    @Field("description_values")
    private Map<String, Object> descriptionValues;
    @Field("type")
    private String type;
    @Field("action_date")
    private Instant actionDate;
    @Field("original_description")
    private String originalDescription;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, Object> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, Object> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Instant getActionDate() {
        return actionDate;
    }

    public void setActionDate(Instant actionDate) {
        this.actionDate = actionDate;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }
}
