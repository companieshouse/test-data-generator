package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.company.Data;

import java.util.List;

public class CompanyDetailsResponse extends Data {

    @JsonProperty("accounts")
    public Accounts accounts;

    @JsonProperty("can_file")
    public boolean canFile;

    @JsonProperty("company_name")
    public String companyName;

    @JsonProperty("company_number")
    public String companyNumber;

    @JsonProperty("company_status")
    public String companyStatus;

    @JsonProperty("company_status_detail")
    public String companyStatusDetail;

    @JsonProperty("confirmation_statement")
    public ConfirmationStatement confirmationStatement;

    @JsonProperty("date_of_creation")
    public String dateOfCreation;

    @JsonProperty("delta_at")
    public String deltaAt;

    @JsonProperty("updated")
    public Updated updated;

    @JsonProperty("etag")
    public String etag;

    @JsonProperty("has_charges")
    public boolean hasCharges;

    @JsonProperty("has_insolvency_history")
    public boolean hasInsolvencyHistory;

    @JsonProperty("has_super_secure_pscs")
    public boolean hasSuperSecurePscs;

    @JsonProperty("jurisdiction")
    public String jurisdiction;

    @JsonProperty("links")
    public Links links;

    @JsonProperty("registered_office_address")
    public Address registeredOfficeAddress;

    @JsonProperty("registered_office_is_in_dispute")
    public boolean registeredOfficeIsInDispute;

    @JsonProperty("service_address")
    public Address serviceAddress;

    @JsonProperty("super_secure_managing_officer_count")
    public Integer superSecureManagingOfficerCount;

    @JsonProperty("sic_codes")
    public List<String> sicCodes;

    @JsonProperty("type")
    public String type;

    @JsonProperty("undeliverable_registered_office_address")
    public boolean undeliverableRegisteredOfficeAddress;

    @JsonProperty("test_data")
    public Boolean testData;

    @JsonProperty("foreign_company_details")
    public ForeignCompanyDetails foreignCompanyDetails;

    public static class Accounts {
        @JsonProperty("accounting_reference_date")
        public DayMonth accountingReferenceDate;

        @JsonProperty("next_accounts")
        public NextAccounts nextAccounts;

        @JsonProperty("last_accounts")
        public LastAccounts lastAccounts;

        @JsonProperty("next_due")
        public String nextDue;

        @JsonProperty("next_made_up_to")
        public String nextMadeUpTo;

        @JsonProperty("overdue")
        public boolean overdue;
    }

    public static class DayMonth {
        @JsonProperty("day")
        public String day;

        @JsonProperty("month")
        public String month;
    }

    public static class NextAccounts {
        @JsonProperty("due_on")
        public String dueOn;

        @JsonProperty("overdue")
        public boolean overdue;

        @JsonProperty("period_end_on")
        public String periodEndOn;

        @JsonProperty("period_start_on")
        public String periodStartOn;
    }

    public static class LastAccounts {
        @JsonProperty("type")
        public String type;

        @JsonProperty("period_start_on")
        public String periodStartOn;

        @JsonProperty("period_end_on")
        public String periodEndOn;

        @JsonProperty("made_up_to")
        public String madeUpTo;
    }

    public static class ConfirmationStatement {
        @JsonProperty("next_due")
        public String nextDue;

        @JsonProperty("next_made_up_to")
        public String nextMadeUpTo;

        @JsonProperty("overdue")
        public boolean overdue;
    }

    public static class Updated {
        @JsonProperty("at")
        public String at;

        @JsonProperty("by")
        public String by;

        @JsonProperty("type")
        public String type;
    }

    public static class Links {
        @JsonProperty("self")
        public String self;

        @JsonProperty("filing_history")
        public String filingHistory;

        @JsonProperty("officers")
        public String officers;

        @JsonProperty("persons_with_significant_control_statement")
        public String personsWithSignificantControlStatement;

        @JsonProperty("registers")
        public String registers;
    }

    public static class Address {
        @JsonProperty("address_line_1")
        public String addressLine1;

        @JsonProperty("address_line_2")
        public String addressLine2;

        @JsonProperty("country")
        public String country;

        @JsonProperty("locality")
        public String locality;

        @JsonProperty("postal_code")
        public String postalCode;

        @JsonProperty("premise")
        public String premise;
    }

    public static class ForeignCompanyDetails {
        @JsonProperty("governed_by")
        public String governedBy;

        @JsonProperty("registration_number")
        public String registrationNumber;

        @JsonProperty("legal_form")
        public String legalForm;

        @JsonProperty("business_activity")
        public String businessActivity;

        @JsonProperty("is_a_credit_financial_institution")
        public boolean isACreditFinancialInstitution;

        @JsonProperty("originating_registry")
        public OriginatingRegistry originatingRegistry;

        @JsonProperty("accounting_requirement")
        public AccountingRequirement accountingRequirement;

        @JsonProperty("accounts")
        public ForeignAccounts accounts;
    }

    public static class OriginatingRegistry {
        @JsonProperty("country")
        public String country;

        @JsonProperty("name")
        public String name;
    }

    public static class AccountingRequirement {
        @JsonProperty("foreign_account_type")
        public String foreignAccountType;

        @JsonProperty("terms_of_account_publication")
        public String termsOfAccountPublication;
    }

    public static class ForeignAccounts {
        @JsonProperty("account_period_from")
        public DayMonth accountPeriodFrom;

        @JsonProperty("account_period_to")
        public DayMonth accountPeriodTo;

        @JsonProperty("must_file_within")
        public MustFileWithin mustFileWithin;
    }

    public static class MustFileWithin {
        @JsonProperty("months")
        public int months;
    }
}


