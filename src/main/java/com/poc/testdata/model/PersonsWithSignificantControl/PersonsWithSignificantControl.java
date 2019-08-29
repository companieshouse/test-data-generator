package com.poc.testdata.model.PersonsWithSignificantControl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Links;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "company_pscs")
public class PersonsWithSignificantControl {

    @Id
    private String id;
    @Field("active_count")
    private Integer activeCount;
    @Field("ceased_count")
    private Integer ceasedCount;
    @Field("etag")
    private String etag;
    @Field("items")
    private List<PersonsWithSignificantControlItem> items;
    @Field("items_per_page")
    private Integer itemsPerPage;
    @Field("kind")
    private String kind;
    @Field("links")
    private Links links;
    @Field("start_index")
    private Integer startIndex;
    @Field("total_results")
    private Integer totalResults;
    @Field("company_number")
    private String companyNumber;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Integer activeCount) {
        this.activeCount = activeCount;
    }

    public Integer getCeasedCount() {
        return ceasedCount;
    }

    public void setCeasedCount(Integer ceasedCount) {
        this.ceasedCount = ceasedCount;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public List<PersonsWithSignificantControlItem> getItems() {
        return items;
    }

    public void setItems(List<PersonsWithSignificantControlItem> items) {
        this.items = items;
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

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Integer getStartIndex() {
        return startIndex;
    }

    public void setStartIndex(Integer startIndex) {
        this.startIndex = startIndex;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
