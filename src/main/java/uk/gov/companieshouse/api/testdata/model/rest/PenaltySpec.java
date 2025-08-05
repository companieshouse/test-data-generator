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

    @JsonProperty("amount")
    @NotNull(message = "Amount must not be blank")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @JsonProperty("type_description")
    private String typeDescription;

    @JsonProperty("ledger_code")
    private String ledgerCode;

    @JsonProperty("dunning_status")
    private String dunningStatus;

    @JsonProperty("closed_at")
    private Instant closedAt;

    @JsonProperty("is_paid")
    private Boolean isPaid;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("transaction_sub_type")
    private String transactionSubType;

    @JsonProperty("account_status")
    private String accountStatus;

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

    public String getLedgerCode() {
        return ledgerCode;
    }

    public String getDunningStatus() {
        return dunningStatus;
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
        return transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

}
