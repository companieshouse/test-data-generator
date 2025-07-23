package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.util.List;
import org.bson.types.ObjectId;

public class AccountPenaltiesData {

    @JsonProperty("_id")
    private ObjectId id;

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

    @JsonProperty("_id")
    public String getIdAsString() {
        return id != null ? id.toHexString() : null;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setPenalties(
            List<PenaltyData> penalties) {
        this.penalties = penalties;
    }
}
