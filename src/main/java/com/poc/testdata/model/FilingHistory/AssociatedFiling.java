package com.poc.testdata.model.FilingHistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class AssociatedFiling {

    @Field("category")
    private String category;
    @Field("date")
    private Date date;
    @Field("description")
    private String description;
    @Field("description_values")
    private DescriptionValue descriptionValue;
    @Field("type")
    private String type;

    AssociatedFiling (){}

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DescriptionValue getDescriptionValue() {
        return descriptionValue;
    }

    public void setDescriptionValue(DescriptionValue descriptionValue) {
        this.descriptionValue = descriptionValue;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
