
package com.poc.testdata.model.CompanyProfile;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.poc.testdata.model.Links;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.List;


@Document(collection = "company_profile")
public class Company {

    @Id
    @Field("id")
    private String id;
    @Field("accounts")
    private Accounts accounts;
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
    @Field("annual_return")
    private AnnualReturn annualReturn;
    @Field("can_file")
    private Boolean canFile;
    @Field("company_name")
    private String companyName;
    @Field("company_number")
    private String companyNumber;
    @Field("company_status")
    private String companyStatus;
    @Field("company_status_detail")
    private String companyStatusDetail;
    @Field("confirmation_statement")
    private ConfirmationStatement confirmationStatement;
    @Field("date_of_cessation")
    private Date dateOfCessation;
    @Field("date_of_creation")
    private Date dateOfCreation;
    @Field("etag")
    private String etag;
    @Field("external_registration_number")
    private String externalRegistrationNumber;
    @Field("has_been_liquidated")
    private Boolean hasBeenLiquidated;
    @Field("has_charges")
    private Boolean hasCharges;
    @Field("has_insolvency_history")
    private Boolean hasInsolvencyHistory;
    @Field("is_community_interest_company")
    private Boolean isCommunityInterestCompany;
    @Field("jurisdiction")
    private String jurisdiction;
    @Field("last_full_members_list_date")
    private Date lastFullMembersListDate;
    @Field("links")
    private Links links;
    @Field("partial_data_available")
    private String partialDataAvailable;
    @Field("previous_company_names")
    private List<PreviousCompanyName> previousCompanyNames = null;
    @Field("registered_office_address")
    private RegisteredOfficeAddress registeredOfficeAddress;
    @Field("registered_office_is_in_dispute")
    private Boolean registeredOfficeIsInDispute;
    @Field("sic_codes")
    private List<String> sicCodes = null;
    @Field("subtype")
    private String subtype;
    @Field("type")
    private String type;
    @Field("undeliverable_registered_office_address")
    private Boolean undeliverableRegisteredOfficeAddress;


    /**
     * No args constructor for use in serialization
     * 
     */
    public Company() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Accounts getAccounts() {
        return accounts;
    }

    public void setAccounts(Accounts accounts) {
        this.accounts = accounts;
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

    public AnnualReturn getAnnualReturn() {
        return annualReturn;
    }

    public void setAnnualReturn(AnnualReturn annualReturn) {
        this.annualReturn = annualReturn;
    }

    public Boolean getCanFile() {
        return canFile;
    }

    public void setCanFile(Boolean canFile) {
        this.canFile = canFile;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public String getCompanyStatusDetail() {
        return companyStatusDetail;
    }

    public void setCompanyStatusDetail(String companyStatusDetail) {
        this.companyStatusDetail = companyStatusDetail;
    }

    public ConfirmationStatement getConfirmationStatement() {
        return confirmationStatement;
    }

    public void setConfirmationStatement(ConfirmationStatement confirmationStatement) {
        this.confirmationStatement = confirmationStatement;
    }

    public Date getDateOfCessation() {
        return dateOfCessation;
    }

    public void setDateOfCessation(Date dateOfCessation) {
        this.dateOfCessation = dateOfCessation;
    }

    public Date getDateOfCreation() {
        return dateOfCreation;
    }

    public void setDateOfCreation(Date dateOfCreation) {
        this.dateOfCreation = dateOfCreation;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getExternalRegistrationNumber() {
        return externalRegistrationNumber;
    }

    public void setExternalRegistrationNumber(String externalRegistrationNumber) {
        this.externalRegistrationNumber = externalRegistrationNumber;
    }

    public Boolean getHasBeenLiquidated() {
        return hasBeenLiquidated;
    }

    public void setHasBeenLiquidated(Boolean hasBeenLiquidated) {
        this.hasBeenLiquidated = hasBeenLiquidated;
    }

    public Boolean getHasCharges() {
        return hasCharges;
    }

    public void setHasCharges(Boolean hasCharges) {
        this.hasCharges = hasCharges;
    }

    public Boolean getHasInsolvencyHistory() {
        return hasInsolvencyHistory;
    }

    public void setHasInsolvencyHistory(Boolean hasInsolvencyHistory) {
        this.hasInsolvencyHistory = hasInsolvencyHistory;
    }

    public Boolean getCommunityInterestCompany() {
        return isCommunityInterestCompany;
    }

    public void setCommunityInterestCompany(Boolean communityInterestCompany) {
        isCommunityInterestCompany = communityInterestCompany;
    }

    public String getJurisdiction() {
        return jurisdiction;
    }

    public void setJurisdiction(String jurisdiction) {
        this.jurisdiction = jurisdiction;
    }

    public Date getLastFullMembersListDate() {
        return lastFullMembersListDate;
    }

    public void setLastFullMembersListDate(Date lastFullMembersListDate) {
        this.lastFullMembersListDate = lastFullMembersListDate;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public String getPartialDataAvailable() {
        return partialDataAvailable;
    }

    public void setPartialDataAvailable(String partialDataAvailable) {
        this.partialDataAvailable = partialDataAvailable;
    }

    public List<PreviousCompanyName> getPreviousCompanyNames() {
        return previousCompanyNames;
    }

    public void setPreviousCompanyNames(List<PreviousCompanyName> previousCompanyNames) {
        this.previousCompanyNames = previousCompanyNames;
    }

    public RegisteredOfficeAddress getRegisteredOfficeAddress() {
        return registeredOfficeAddress;
    }

    public void setRegisteredOfficeAddress(RegisteredOfficeAddress registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public Boolean getRegisteredOfficeIsInDispute() {
        return registeredOfficeIsInDispute;
    }

    public void setRegisteredOfficeIsInDispute(Boolean registeredOfficeIsInDispute) {
        this.registeredOfficeIsInDispute = registeredOfficeIsInDispute;
    }

    public List<String> getSicCodes() {
        return sicCodes;
    }

    public void setSicCodes(List<String> sicCodes) {
        this.sicCodes = sicCodes;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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
}
