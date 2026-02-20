package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import uk.gov.companieshouse.api.testdata.model.rest.enums.PenaltiesTransactionSubType;

import java.time.Instant;

public class PenaltySpec {

    @JsonProperty("customer_code")
    @NotNull(message = "Customer code must not be blank")
    private String customerCode;

    @JsonProperty("company_code")
    @NotNull(message = "Company code must not be blank")
    @Pattern(regexp = "LP|C1",
            message = "Invalid Company Code")
    private String companyCode;

    @JsonProperty("number_of_penalties")
    @NotNull(message = "Number of penalties must not be blank")
    @Min(value = 1, message = "Number of penalties must be at least 1")
    @Max(value = 20, message = "Number of penalties must not exceed 20")
    private Integer numberOfPenalties;

    @JsonProperty("amount")
    @Positive(message = "Amount must be a positive number")
    private Double amount;

    @JsonProperty("part_payment")
    private Boolean partPaid;

    @JsonProperty("type_description")
    @Pattern(regexp = "EOCFP|EOJSD|PENU|CS01|CS01 IDV",
            message = "Invalid type description")
    private String typeDescription;

    @JsonProperty("ledger_code")
    @Pattern(regexp = "EW|SC|NI|E1|S1|N1|FU",
            message = "Invalid ledger code")
    private String ledgerCode;

    @JsonProperty("dunning_status")
    @Pattern(regexp = "PEN1|PEN2|PEN3|DCA", message = "invalid dunning status option")
    private String dunningStatus;

    @JsonProperty("closed_at")
    private Instant closedAt;

    @JsonProperty("is_paid")
    private Boolean isPaid;

    @JsonProperty("transaction_type")
    private String transactionType;

    @JsonProperty("transaction_sub_type")
    private PenaltiesTransactionSubType transactionSubType;

    @JsonProperty("account_status")
    private String accountStatus;

    @JsonProperty("duplicate")
    private boolean duplicate = false;

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

    public Boolean getPartPaid() {
        return partPaid;
    }

    public void setPartPaid(Boolean partPaid) {
        this.partPaid = partPaid;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public PenaltiesTransactionSubType getTransactionSubType() {
        return transactionSubType;
    }

    public void setTransactionSubType(PenaltiesTransactionSubType transactionSubType) {
        this.transactionSubType = transactionSubType;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public boolean isDuplicate() {
        return duplicate;
    }

    public void setDuplicate(boolean duplicate) {
        this.duplicate = duplicate;
    }
}
