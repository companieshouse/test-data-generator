
package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "company_profile")
public class CompanyProfile {

    public class Accounts {
        @Field("next_due")
        private Instant nextDue;
        @Field("next_accounts.period_start_on")
        private Instant periodStart;
        @Field("next_accounts.period_end_on")
        private Instant periodEnd;
        @Field("next_accounts.due_on")
        private Instant nextAccountsDueOn;
        @Field("next_accounts.overdue")
        private Boolean nextAccountsOverdue;
        @Field("next_made_up_to")
        private Instant nextMadeUpTo;
        @Field("accounting_reference_date.day")
        private String accountingReferenceDateDay;
        @Field("accounting_reference_date.month")
        private String accountingReferenceDateMonth;

        public Instant getNextDue() {
            return nextDue;
        }

        public void setNextDue(Instant nextDue) {
            this.nextDue = nextDue;
        }

        public Instant getPeriodStart() {
            return periodStart;
        }

        public void setPeriodStart(Instant periodStart) {
            this.periodStart = periodStart;
        }

        public Instant getPeriodEnd() {
            return periodEnd;
        }

        public void setPeriodEnd(Instant periodEnd) {
            this.periodEnd = periodEnd;
        }

        public Instant getNextAccountsDueOn() {
            return nextAccountsDueOn;
        }

        public void setNextAccountsDueOn(Instant nextAccountsDueOn) {
            this.nextAccountsDueOn = nextAccountsDueOn;
        }

        public Boolean getNextAccountsOverdue() {
            return nextAccountsOverdue;
        }

        public void setNextAccountsOverdue(Boolean nextAccountsOverdue) {
            this.nextAccountsOverdue = nextAccountsOverdue;
        }

        public Instant getNextMadeUpTo() {
            return nextMadeUpTo;
        }

        public void setNextMadeUpTo(Instant nextMadeUpTo) {
            this.nextMadeUpTo = nextMadeUpTo;
        }

        public String getAccountingReferenceDateDay() {
            return accountingReferenceDateDay;
        }

        public void setAccountingReferenceDateDay(String accountingReferenceDateDay) {
            this.accountingReferenceDateDay = accountingReferenceDateDay;
        }

        public String getAccountingReferenceDateMonth() {
            return accountingReferenceDateMonth;
        }

        public void setAccountingReferenceDateMonth(String accountingReferenceDateMonth) {
            this.accountingReferenceDateMonth = accountingReferenceDateMonth;
        }
    }

    public class ConfirmationStatement {
        @Field("next_made_up_to")
        private Instant nextMadeUpTo;
        @Field("overdue")
        private Boolean overdue;
        @Field("next_due")
        private Instant nextDue;

        public Instant getNextMadeUpTo() {
            return nextMadeUpTo;
        }

        public void setNextMadeUpTo(Instant nextMadeUpTo) {
            this.nextMadeUpTo = nextMadeUpTo;
        }

        public Boolean getOverdue() {
            return overdue;
        }

        public void setOverdue(Boolean overdue) {
            this.overdue = overdue;
        }

        public Instant getNextDue() {
            return nextDue;
        }

        public void setNextDue(Instant nextDue) {
            this.nextDue = nextDue;
        }
    }

    @Id
    @Field("id")
    private String id;
    @Field("links")
    private Links links;
    @Field("accounts")
    private Accounts accounts = new Accounts();
    @Field("company_number")
    private String companyNumber;
    @Field("date_of_creation")
    private Instant dateOfCreation;
    @Field("type")
    private String type;
    @Field("undeliverable_registered_office_address")
    private Boolean undeliverableRegisteredOfficeAddress;
    @Field("company_name")
    private String companyName;
    @Field("sic_codes")
    private List<String> sicCodes;
    @Field("confirmation_statement")
    private ConfirmationStatement confirmationStatement = new ConfirmationStatement();
    @Field("registered_office_is_in_dispute")
    private Boolean registeredOfficeIsInDispute;
    @Field("company_status")
    private String companyStatus;
    @Field("etag")
    private String etag;
    @Field("has_insolvency_history")
    private Boolean hasInsolvencyHistory;
    @Field("registered_office_address")
    private Address registeredOfficeAddress;
    @Field("jurisdiction")
    private String jurisdiction;
    @Field("has_charges")
    private Boolean hasCharges;
    @Field("can_file")
    private Boolean canFile;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Instant getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Instant dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getUndeliverableRegisteredOfficeAddress() {
        return undeliverableRegisteredOfficeAddress;
    }

    public void setUndeliverableRegisteredOfficeAddress(Boolean undeliverableRegisteredOfficeAddress) {
        this.undeliverableRegisteredOfficeAddress = undeliverableRegisteredOfficeAddress;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public ConfirmationStatement getConfirmationStatement() {
        return confirmationStatement;
    }

    public Boolean getRegisteredOfficeIsInDispute() {
        return registeredOfficeIsInDispute;
    }

    public void setRegisteredOfficeIsInDispute(Boolean registeredOfficeIsInDispute) {
        this.registeredOfficeIsInDispute = registeredOfficeIsInDispute;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Boolean getHasInsolvencyHistory() {
        return hasInsolvencyHistory;
    }

    public void setHasInsolvencyHistory(Boolean hasInsolvencyHistory) {
        this.hasInsolvencyHistory = hasInsolvencyHistory;
    }

    public Address getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public Boolean getHasCharges() {
        return hasCharges;
    }

    public void setHasCharges(Boolean hasCharges) {
        this.hasCharges = hasCharges;
    }

    public Boolean getCanFile() {
        return canFile;
    }

    public void setCanFile(Boolean canFile) {
        this.canFile = canFile;
    }
}
