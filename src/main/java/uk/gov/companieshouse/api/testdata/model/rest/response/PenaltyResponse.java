package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PenaltyResponse {

    @JsonProperty("company_code")
    private String companyCode;

    @JsonProperty("customer_code")
    private String customerCode;

    @JsonProperty("transaction_reference")
    private String transactionReference;

    @JsonProperty("transaction_date")
    private String transactionDate;

    @JsonProperty("made_up_date")
    private String madeUpDate;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("outstanding_amount")
    private Double outstandingAmount;

    @JsonProperty("is_paid")
    private boolean isPaid;

    @JsonProperty("account_status")
    private String accountStatus;

    @JsonProperty("dunning_status")
    private String dunningStatus;

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

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getMadeUpDate() {
        return madeUpDate;
    }

    public void setMadeUpDate(String madeUpDate) {
        this.madeUpDate = madeUpDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(Double outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public boolean getIsPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean paid) {
        isPaid = paid;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getDunningStatus() {
        return dunningStatus;
    }

    public void setDunningStatus(String dunningStatus) {
        this.dunningStatus = dunningStatus;
    }
}
