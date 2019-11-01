package uk.gov.companieshouse.api.testdata.model.officer;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.testdata.model.Links;

import java.util.List;

@Document(collection = "officer_listing")
public class Officer {

    @Id
    @Field("id")
    private String id;
    @Field("active_count")
    private Integer activeCount;
    @Field("inactive_count")
    private Integer inactiveCount;
    @Field("items")
    private List<OfficerItem> officerItems = null;
    @Field("links")
    private Links links;
    @Field("resigned_count")
    private Integer resignedCount;
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

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
