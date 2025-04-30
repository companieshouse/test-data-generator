package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class AccountPenalty {

    @Field("company_code")
    private String companyCode;

    @Field("ledger_code")
    private String ledgerCode;

    @Field("customer_code")
    private String customerCode;

    @Field("transaction_reference")
    private String transactionReference;

    @Field("transaction_date")
    private String transactionDate;

    @Field("made_up_date")
    private String madeUpDate;

    @Field("amount")
    private Double amount;

    @Field("outstanding_amount")
    private Double outstandingAmount;

    @Field("is_paid")
    private boolean isPaid;

    @Field("transaction_type")
    private String transactionType;

    @Field("transaction_sub_type")
    private String transactionSubType;

    @Field("type_description")
    private String typeDescription;

    @Field("due_date")
    private String dueDate;

    @Field("account_status")
    private String accountStatus;

    @Field("dunning_status")
    private String dunningStatus;

    public String getCompanyCode() {
        return companyCode;
    }

    public void setCompanyCode(String companyCode) {
        this.companyCode = companyCode;
    }

    public String getLedgerCode() {
        return ledgerCode;
    }

    public void setLedgerCode(String ledgerCode) {
        this.ledgerCode = ledgerCode;
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

    public boolean isPaid() {
        return isPaid;
    }

    public void setIsPaid(boolean paid) {
        isPaid = paid;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(String transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
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
