package com.poc.testdata.model.FilingHistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Links;
import org.springframework.context.annotation.Profile;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "company_filing_history")
public class FilingHistory {

    @Id
    @Field("id")
    private String id;
    @Field("etag")
    private String etag;
    @Field("filing_history_status")
    private String filingHistoryStatus;
    @Field("items")
    private List<FilingHistoryItem> filingHistoryItems = null;
    @Field("items_per_page")
    private Integer itemsPerPage;
    @Field("kind")
    private String kind;
    @Field("start_index")
    private Integer startIndex;
    @Field("total_count")
    private Integer totalCount;
    @Field("company_number")
    private String companyNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getFilingHistoryStatus() {
        return filingHistoryStatus;
    }

    public void setFilingHistoryStatus(String filingHistoryStatus) {
        this.filingHistoryStatus = filingHistoryStatus;
    }

    public List<FilingHistoryItem> getFilingHistoryItems() {
        return filingHistoryItems;
    }

    public void setFilingHistoryItems(List<FilingHistoryItem> filingHistoryItems) {
        this.filingHistoryItems = filingHistoryItems;
    }

    public Integer getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(Integer itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

}
