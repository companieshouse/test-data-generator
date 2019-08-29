package com.poc.testdata.model.FilingHistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class Resolution {

    @Field("category")
    private String category;
    @Field("description")
    private String description;
    @Field("description_values")
    private DescriptionValue descriptionValue;
    @Field("document_id")
    private String documentId;
    @Field("receive_date")
    private Date receiveDate;
    @Field("subcategory")
    private String subcategory;
    @Field("type")
    private String type;

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

    public DescriptionValue getDescriptionValue() {
        return descriptionValue;
    }

    public void setDescriptionValue(DescriptionValue descriptionValue) {
        this.descriptionValue = descriptionValue;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Date getReceiveDate() {
        return receiveDate;
    }

    public void setReceiveDate(Date receiveDate) {
        this.receiveDate = receiveDate;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
