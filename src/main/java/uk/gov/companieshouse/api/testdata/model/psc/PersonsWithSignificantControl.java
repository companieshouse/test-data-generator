package uk.gov.companieshouse.api.testdata.model.psc;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import uk.gov.companieshouse.api.testdata.model.Links;

import java.util.List;

@Document(collection = "company_pscs")
public class PersonsWithSignificantControl {

    @Id
    private String id;
    @Field("active_count")
    private Integer activeCount;
    @Field("ceased_count")
    private Integer ceasedCount;
    @Field("items")
    private List<PersonsWithSignificantControlItem> items;
    @Field("links")
    private Links links;
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

    public List<PersonsWithSignificantControlItem> getItems() {
        return items;
    }

    public void setItems(List<PersonsWithSignificantControlItem> items) {
        this.items = items;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }
}
