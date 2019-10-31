package uk.gov.companieshouse.api.testdata.model.filinghistory;

import uk.gov.companieshouse.api.testdata.model.Links;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;

public class FilingHistoryItem {

    @Field("category")
    private String category;
    @Field("date")
    private Date date;
    @Field("description")
    private String description;
    @Field("links")
    private Links links;
    @Field("transaction_id")
    private String transactionId;
    @Field("type")
    private String type;

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

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
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
