package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class FilingHistoryDocument {

    @Field("filing_history_date")
    private String filingHistoryDate;

    @Field("filing_history_description")
    private String filingHistoryDescription;

    @Field("filing_history_description_values")
    private FilingHistoryDescriptionValues filingHistoryDescriptionValues;

    @Field("filing_history_id")
    private String filingHistoryId;

    @Field("filing_history_type")
    private String filingHistoryType;

    @Field("filing_history_cost")
    private String filingHistoryCost;

    @Field("filing_history_category")
    private String filingHistoryCategory;

    @Field("filing_history_barcode")
    private String filingHistoryBarcode;

    public String getFilingHistoryDate() {
        return filingHistoryDate;
    }

    public void setFilingHistoryDate(String filingHistoryDate) {
        this.filingHistoryDate = filingHistoryDate;
    }

    public String getFilingHistoryDescription() {
        return filingHistoryDescription;
    }

    public void setFilingHistoryDescription(String filingHistoryDescription) {
        this.filingHistoryDescription = filingHistoryDescription;
    }

    public FilingHistoryDescriptionValues getFilingHistoryDescriptionValues() {
        return filingHistoryDescriptionValues;
    }

    public void setFilingHistoryDescriptionValues(FilingHistoryDescriptionValues values) {
        this.filingHistoryDescriptionValues = values;
    }

    public String getFilingHistoryId() {
        return filingHistoryId;
    }

    public void setFilingHistoryId(String filingHistoryId) {
        this.filingHistoryId = filingHistoryId;
    }

    public String getFilingHistoryType() {
        return filingHistoryType;
    }

    public void setFilingHistoryType(String filingHistoryType) {
        this.filingHistoryType = filingHistoryType;
    }

    public String getFilingHistoryCost() {
        return filingHistoryCost;
    }

    public void setFilingHistoryCost(String filingHistoryCost) {
        this.filingHistoryCost = filingHistoryCost;
    }

    public String getFilingHistoryCategory() {
        return  filingHistoryCategory;
    }

    public void setFilingHistoryCategory(String filingHistoryCategory) {
        this.filingHistoryCategory = filingHistoryCategory;
    }

    public String getFilingHistoryBarcode() {
        return filingHistoryBarcode;
    }

    public void setFilingHistoryBarcode(String filingHistoryBarcode) {
        this.filingHistoryBarcode = filingHistoryBarcode;
    }
}
