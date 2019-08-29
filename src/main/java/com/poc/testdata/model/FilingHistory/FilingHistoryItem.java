package com.poc.testdata.model.FilingHistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Links;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

public class FilingHistoryItem {

    @Field("annotations")
    private List<Annotation> annotations = null;
    @Field("accociated_filings")
    private List<AssociatedFiling> associatedFilings = null;
    @Field("barcode")
    private String barcode;
    @Field("category")
    private String category;
    @Field("date")
    private Date date;
    @Field("description")
    private String description;
    @Field("description_values")
    private DescriptionValue descriptionValue;
    @Field("links")
    private Links links;
    @Field("pages")
    private Integer pages;
    @Field("paper_filed")
    private Boolean paperFiled;
    @Field("resolutions")
    private List<Resolution> resolutions = null;
    @Field("sub_category")
    private String subCategory;
    @Field("transaction_id")
    private String transactionId;
    @Field("type")
    private String type;


    public List<Annotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Annotation> annotations) {
        this.annotations = annotations;
    }

    public List<AssociatedFiling> getAssociatedFilings() {
        return associatedFilings;
    }

    public void setAssociatedFilings(List<AssociatedFiling> associatedFilings) {
        this.associatedFilings = associatedFilings;
    }

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

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public Boolean getPaperFiled() {
        return paperFiled;
    }

    public void setPaperFiled(Boolean paperFiled) {
        this.paperFiled = paperFiled;
    }

    public List<Resolution> getResolutions() {
        return resolutions;
    }

    public void setResolutions(List<Resolution> resolutions) {
        this.resolutions = resolutions;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
