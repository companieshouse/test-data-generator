
package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    @Field("data.links")
    private Links links;
    @Field("data.accounts")
    private Accounts accounts = new Accounts();
    @Field("data.company_number")
    private String companyNumber;
    @Field("data.date_of_creation")
    private Instant dateOfCreation;
    @Field("data.type")
    private String type;
    @Field("data.undeliverable_registered_office_address")
    private Boolean undeliverableRegisteredOfficeAddress;
    @Field("data.has_super_secure_pscs")
    private Boolean hasSuperSecurePscs;
    @Field("data.company_name")
    private String companyName;
    @Field("data.sic_codes")
    private List<String> sicCodes;
    @Field("data.confirmation_statement")
    private ConfirmationStatement confirmationStatement = new ConfirmationStatement();
    @Field("data.registered_office_is_in_dispute")
    private Boolean registeredOfficeIsInDispute;
    @Field("data.company_status")
    private String companyStatus;
    @Field("data.etag")
    private String etag;
    @Field("data.has_insolvency_history")
    private Boolean hasInsolvencyHistory;
    @Field("data.registered_office_address")
    private Address registeredOfficeAddress;
    @Field("data.jurisdiction")
    private String jurisdiction;
    @Field("data.has_charges")
    private Boolean hasCharges;
    @Field("data.can_file")
    private Boolean canFile;
    @Field("data.subtype")
    private String subtype;
    @Field("data.is_community_interest_company")
    private Boolean isCommunityInterestCompany;
    @Field ("data.company_status_detail")
    private String companyStatusDetail;
    @Field("data.partial_data_available")
    private String partialDataAvailable;

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

    public void setUndeliverableRegisteredOfficeAddress(
            Boolean undeliverableRegisteredOfficeAddress) {
        this.undeliverableRegisteredOfficeAddress = undeliverableRegisteredOfficeAddress;
    }

    public Boolean getHasSuperSecurePscs() {
        return hasSuperSecurePscs;
    }

    public void setHasSuperSecurePscs(Boolean hasSuperSecurePscs) {
        this.hasSuperSecurePscs = hasSuperSecurePscs;
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

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Boolean getIsCommunityInterestCompany() {
        return isCommunityInterestCompany;
    }

    public void setIsCommunityInterestCompany(Boolean isCommunityInterestCompany) {
        this.isCommunityInterestCompany = isCommunityInterestCompany;
    }

    public String getCompanyStatusDetail() {
        return companyStatusDetail;
    }

    public void setCompanyStatusDetail(String companyStatusDetail) {
        this.companyStatusDetail = companyStatusDetail;
    }

    public String getPartialDataAvailable() {return this.partialDataAvailable; }

    public void setPartialDataAvailable(String partialDataAvailable) {
        this.partialDataAvailable = partialDataAvailable;
    }
}
