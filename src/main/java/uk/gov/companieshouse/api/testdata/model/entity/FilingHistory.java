package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "company_filing_history")
public class FilingHistory {

    @Id
    @Field("id")
    private String id;
    @Field("company_number")
    private String companyNumber;
    @Field("data.links")
    private Links links;
    @Field("data.associated_filings")
    private List<AssociatedFiling> associatedFilings;
    @Field("data.resolutions")
    private List<Resolutions> resolutions;
    @Field("data.category")
    private String category;
    @Field("data.description")
    private String description;
    @Field("data.date")
    private Instant date;
    @Field("data.type")
    private String type;
    @Field("data.subcategory")
    private String subCategory;
    @Field("data.pages")
    private Integer pages;
    @Field ("data.action_date")
    private Instant actionDate;
    @Field ("data.description_values")
    private DescriptionValues descriptionValues;
    @Field("data.original_values")
    private OriginalValues originalValues;
    @Field("data.paper_filed")
    private boolean paperFiled;
    @Field("_entity_id")
    private String entityId;
    @Field("original_description")
    private String originalDescription;
    @Field("barcode")
    private String barcode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public List<AssociatedFiling> getAssociatedFilings() {
        return associatedFilings;
    }

    public void setAssociatedFilings(List<AssociatedFiling> associatedFilings) {
        this.associatedFilings = associatedFilings;
    }

    public List<Resolutions> getResolutions() { return resolutions; }

    public void setResolutions(List<Resolutions> resolutions) {
        this.resolutions = resolutions;
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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
        this.pages = pages;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getOriginalDescription() {
        return originalDescription;
    }

    public void setOriginalDescription(String originalDescription) {
        this.originalDescription = originalDescription;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public Instant getActionDate() {
        return  actionDate;
    }

    public void setActionDate(Instant actionDate) {
        this.actionDate = actionDate;
    }

    public DescriptionValues getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(DescriptionValues descriptionValues) {
        this.descriptionValues = descriptionValues;
    }

    public OriginalValues getOriginalValues() {
        return originalValues;
    }

    public void setOriginalValues(OriginalValues originalValues) {
        this.originalValues = originalValues;
    }

    public void setPaperFiled(boolean paperFiled) {
        this.paperFiled = paperFiled;
    }

    public boolean isPaperFiled() {
        return paperFiled;
    }
}
