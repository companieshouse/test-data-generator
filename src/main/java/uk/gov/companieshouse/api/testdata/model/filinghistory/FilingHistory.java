package uk.gov.companieshouse.api.testdata.model.filinghistory;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "company_filing_history")
public class FilingHistory {

    @Id
    @Field("id")
    private String id;
    @Field("items")
    private List<FilingHistoryItem> filingHistoryItems = null;
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

    public List<FilingHistoryItem> getFilingHistoryItems() {
        return filingHistoryItems;
    }

    public void setFilingHistoryItems(List<FilingHistoryItem> filingHistoryItems) {
        this.filingHistoryItems = filingHistoryItems;
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
