package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public class PenaltySpec {

    @JsonProperty("customer_code")
    @NotNull(message = "Customer code must not be blank")
    private String customerCode;

    @JsonProperty("company_code")
    @NotNull(message = "Company code must not be blank")
    private String companyCode;

    @JsonProperty("number_of_penalties")
    @NotNull(message = "Number of penalties must not be blank")
    @Min(value = 1, message = "Number of penalties must be at least 1")
    @Max(value = 20, message = "Number of penalties must not exceed 20")
    private Integer numberOfPenalties;

    @JsonProperty("type_description")
    private String typeDescription;

    @JsonProperty("ledger_code")
    private String ledgerCode;

    @JsonProperty("dunning_status")
    private String DunningStatus;

    @JsonProperty("closed_at")
    private Instant ClosedAt;

    @JsonProperty("amount")
    @NotNull(message = "Amount must not be blank")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @JsonProperty("is_paid")
    private Boolean isPaid;

    @JsonProperty("transaction_type")
    private String TransactionType;

    @JsonProperty("transaction_sub_type")
    private String TransactionSubType;

    @JsonProperty("account_status")
    private String AccountStatus;

    public Integer getNumberOfPenalties() {
        return numberOfPenalties;
    }

    public void setNumberOfPenalties(Integer numberOfPenalties) {
        this.numberOfPenalties = numberOfPenalties;
    }

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

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getLedgerCode() {
        return ledgerCode;
    }

    public void setLedgerCode(String ledgerCode) {
        this.ledgerCode = ledgerCode;
    }

    public String getDunningStatus() {
        return DunningStatus;
    }

    public void setDunningStatus(String dunningStatus) {
        DunningStatus = dunningStatus;
    }

    public Instant getClosedAt() {
        return ClosedAt;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(Boolean isPaid) {
        this.isPaid = isPaid;
    }

    public String getTransactionType() {
        return TransactionType;
    }

    public String getTransactionSubType() {
        return TransactionSubType;
    }

    public String getAccountStatus() {
        return AccountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        AccountStatus = accountStatus;
    }

}
