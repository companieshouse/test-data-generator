package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FilingHistoryDocumentsSpec {
    @JsonProperty("filing_history_barcode")
    private String filingHistoryBarcode;

    @JsonProperty("filing_history_category")
    private String filingHistoryCategory;

    @JsonProperty("filing_history_date")
    private String filingHistoryDate;

    @JsonProperty("filing_history_description")
    private String filingHistoryDescription;

    @JsonProperty("filing_history_description_values")
    private FilingHistoryDescriptionValuesSpec filingHistoryDescriptionValues;

    @JsonProperty("filing_history_id")
    private String filingHistoryId;

    @JsonProperty("filing_history_type")
    private String filingHistoryType;

    @JsonProperty("filing_history_cost")
    private String filingHistoryCost;

    public String getFilingHistoryCategory() { return filingHistoryCategory; }

    public void setFilingHistoryCategory(String filingHistoryCategory) {
        this.filingHistoryCategory = filingHistoryCategory;
    }

    public String getFilingHistoryDate() { return filingHistoryDate; }

    public void setFilingHistoryDate(String filingHistoryDate) {
        this.filingHistoryDate = filingHistoryDate;
    }

    public String getFilingHistoryDescription() { return filingHistoryDescription; }

    public void setFilingHistoryDescription(String filingHistoryDescription) {
        this.filingHistoryDescription = filingHistoryDescription;
    }

    public FilingHistoryDescriptionValuesSpec getFilingHistoryDescriptionValues() {
        return filingHistoryDescriptionValues;
    }

    public void setFilingHistoryDescriptionValues(FilingHistoryDescriptionValuesSpec filingHistoryDescriptionValues) {
        this.filingHistoryDescriptionValues = filingHistoryDescriptionValues;
    }

    public String getFilingHistoryId() { return filingHistoryId; }

    public void setFilingHistoryId(String filingHistoryId) {
        this.filingHistoryId = filingHistoryId;
    }

    public String getFilingHistoryType() { return filingHistoryType; }

    public void setFilingHistoryType(String filingHistoryType) {
        this.filingHistoryType = filingHistoryType;
    }

    public String getFilingHistoryCost() { return filingHistoryCost; }

    public void setFilingHistoryCost(String filingHistoryCost) {
        this.filingHistoryCost = filingHistoryCost;
    }

    public String getFilingHistoryBarcode() {
        return filingHistoryBarcode;
    }

    public void setFilingHistoryBarcode(String filingHistoryBarcode) {
        this.filingHistoryBarcode = filingHistoryBarcode;
    }
}
