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
    private Links dataLinks;
    @Field("data.associated_filings")
    private List<AssociatedFiling> dataAssociatedFilings;
    @Field("data.category")
    private String dataCategory;
    @Field("data.description")
    private String dataDescription;
    @Field("data.date")
    private Instant dataDate;
    @Field("data.type")
    private String dataType;
    @Field("data.pages")
    private Integer dataPages;
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

    public Links getDataLinks() {
        return dataLinks;
    }

    public void setDataLinks(Links dataLinks) {
        this.dataLinks = dataLinks;
    }

    public List<AssociatedFiling> getDataAssociatedFilings() {
        return dataAssociatedFilings;
    }

    public void setDataAssociatedFilings(List<AssociatedFiling> dataAssociatedFilings) {
        this.dataAssociatedFilings = dataAssociatedFilings;
    }

    public String getDataCategory() {
        return dataCategory;
    }

    public void setDataCategory(String dataCategory) {
        this.dataCategory = dataCategory;
    }

    public String getDataDescription() {
        return dataDescription;
    }

    public void setDataDescription(String dataDescription) {
        this.dataDescription = dataDescription;
    }

    public Instant getDataDate() {
        return dataDate;
    }

    public void setDataDate(Instant dataDate) {
        this.dataDate = dataDate;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Integer getDataPages() {
        return dataPages;
    }

    public void setDataPages(Integer dataPages) {
        this.dataPages = dataPages;
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
}
