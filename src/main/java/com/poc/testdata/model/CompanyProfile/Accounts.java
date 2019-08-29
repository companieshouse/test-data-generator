
package com.poc.testdata.model.CompanyProfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class Accounts {

    @Field("accounting_reference_date")
    private AccountingReferenceDate accountingReferenceDate;
    @Field("last_accounts")
    private LastAccounts lastAccounts;
    @Field("next_accounts")
    private NextAccounts nextAccounts;
    @Field("next_due")
    private Date nextDue;
    @Field("next_made_up_to")
    private Date nextMadeUpTo;
    @Field("overdue")
    private Boolean overdue;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Accounts() {
    }

    @Field("accounting_reference_date")
    public AccountingReferenceDate getAccountingReferenceDate() {
        return accountingReferenceDate;
    }

    @Field("accounting_reference_date")
    public void setAccountingReferenceDate(AccountingReferenceDate accountingReferenceDate) {
        this.accountingReferenceDate = accountingReferenceDate;
    }

    public LastAccounts getLastAccounts() {
        return lastAccounts;
    }

    public void setLastAccounts(LastAccounts lastAccounts) {
        this.lastAccounts = lastAccounts;
    }

    public NextAccounts getNextAccounts() {
        return nextAccounts;
    }

    public void setNextAccounts(NextAccounts nextAccounts) {
        this.nextAccounts = nextAccounts;
    }

    public Date getNextDue() {
        return nextDue;
    }

    public void setNextDue(Date nextDue) {
        this.nextDue = nextDue;
    }

    public Date getNextMadeUpTo() {
        return nextMadeUpTo;
    }

    public void setNextMadeUpTo(Date nextMadeUpTo) {
        this.nextMadeUpTo = nextMadeUpTo;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }
}
