package com.poc.testdata.model.Officer;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.poc.testdata.model.Links;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "officer_listing")
public class Officer {

    @Id
    @Field("id")
    private String id;
    @Field("active_count")
    private Integer activeCount;
    @Field("etag")
    private String etag;
    @Field("inactive_count")
    private Integer inactiveCount;
    @Field("items")
    private List<OfficerItem> officerItems = null;
    @Field("items_per_page")
    private Integer itemsPerPage;
    @Field("kind")
    private String kind;
    @Field("links")
    private Links links;
    @Field("resigned_count")
    private Integer resignedCount;
    @Field("start_index")
    private Integer startIndex;
    @Field("total_results")
    private Integer totalResults;
    @Field("company_number")
    private String companyNumber;

    public Officer() {
    }

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

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Integer getInactiveCount() {
        return inactiveCount;
    }

    public void setInactiveCount(Integer inactiveCount) {
        this.inactiveCount = inactiveCount;
    }

    public List<OfficerItem> getOfficerItems() {
        return officerItems;
    }

    public void setOfficerItems(List<OfficerItem> officerItems) {
        this.officerItems = officerItems;
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

    public Integer getResignedCount() {
        return resignedCount;
    }

    public void setResignedCount(Integer resignedCount) {
        this.resignedCount = resignedCount;
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
