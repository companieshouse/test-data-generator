package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;

public class AccountPenaltiesData {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("company_code")
    private String companyCode;

    @JsonProperty("customer_code")
    private String customerCode;

    @JsonProperty("created_at")
    private Instant createdAt;

    @JsonProperty("closed_at")
    private Instant closedAt;

    @JsonProperty("data")
    private List<PenaltyData> penalties;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getClosedAt() {
        return closedAt;
    }

    public void setClosedAt(Instant closedAt) {
        this.closedAt = closedAt;
    }

    public List<PenaltyData> getPenalties() {
        return penalties;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPenalties(
            List<PenaltyData> penalties) {
        this.penalties = penalties;
    }
}
