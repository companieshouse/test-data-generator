package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "company_profile")
public class OverseasEntity extends CompanyProfile {

    @Field("version")
    private Long version;

    @Field("has_mortgages")
    private Boolean hasMortgages;

    @Field("test_data")
    private Boolean testData;

    @Field("delta_at")
    private Instant deltaAt;

    @Field("updated")
    private Updated updated;

    @Field("data.external_registration_number")
    private String externalRegistrationNumber;

    @Field("data.foreign_company_details")
    private ForeignCompanyDetails foreignCompanyDetails = new ForeignCompanyDetails();

    @Field("data.service_address")
    private Address serviceAddress;

    @Field("data.super_secure_managing_officer_count")
    private Integer superSecureManagingOfficerCount;

    @Field("data.accounts")
    private Accounts accounts = new Accounts();

    public static interface IUpdated {
        void setAt(Instant at);

        void setBy(String by);

        void setType(String type);
    }

    public static interface IForeignCompanyDetails {
        void setGovernedBy(String governedBy);

        String getLegalForm();

        void setLegalForm(String legalForm);

        void setOriginatingRegistry(IOriginatingRegistry originatingRegistry);

        void setCreditFinancialInstitution(Boolean creditFinancialInstitution);

        void setBusinessActivity(String businessActivity);

        void setRegistrationNumber(String registrationNumber);

        void setAccountingRequirement(IAccountingRequirement accountingRequirement);

        void setAccounts(IAccountsDetails accountsDetails);
    }

    public static interface IOriginatingRegistry {
        void setCountry(String country);

        void setName(String name);
    }

    public static interface IAccountingRequirement {
        void setForeignAccountType(String foreignAccountType);

        void setTermsOfAccountPublication(String termsOfAccountPublication);
    }

    public static interface IAccountsDetails {
        void setAccountPeriodFrom(String day, String month);

        void setAccountPeriodTo(String day, String month);

        void setMustFileWithin(IMustFileWithin mustFileWithin);
    }

    public static interface IMustFileWithin {
        void setMonths(int months);
    }

    public static interface IAccounts {
        void setOverdue(Boolean overdue);

        void setNextMadeUpTo(Instant nextMadeUpTo);

        void setNextDue(Instant nextDue);

        void setAccountingReferenceDate(AccountingReferenceDate accountingReferenceDate);

        void setNextAccounts(NextAccounts nextAccounts);

        void setLastAccounts(LastAccounts lastAccounts);
    }

    public static IForeignCompanyDetails createForeignCompanyDetails() {
        return new ForeignCompanyDetails();
    }

    public static IOriginatingRegistry createOriginatingRegistry() {
        return new OriginatingRegistry();
    }

    public static IAccountingRequirement createAccountingRequirement() {
        return new AccountingRequirement();
    }

    public static IAccountsDetails createAccountsDetails() {
        return new AccountsDetails();
    }

    public static IMustFileWithin createMustFileWithin() {
        return new MustFileWithin();
    }

    public static IUpdated createUpdated() {
        return new Updated();
    }

    public static IAccounts createAccounts() {
        return new Accounts();
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setHasMortgages(Boolean hasMortgages) {
        this.hasMortgages = hasMortgages;
    }

    public void setTestData(Boolean testData) {
        this.testData = testData;
    }

    public void setDeltaAt(Instant deltaAt) {
        this.deltaAt = deltaAt;
    }

    public void setUpdated(IUpdated updated) {
        this.updated = (Updated) updated;
    }

    public void setExternalRegistrationNumber(String externalRegistrationNumber) {
        this.externalRegistrationNumber = externalRegistrationNumber;
    }

    public IForeignCompanyDetails getForeignCompanyDetails() {
        return this.foreignCompanyDetails;
    }

    public void setForeignCompanyDetails(IForeignCompanyDetails foreignCompanyDetails) {
        this.foreignCompanyDetails = (ForeignCompanyDetails) foreignCompanyDetails;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setSuperSecureManagingOfficerCount(Integer superSecureManagingOfficerCount) {
        this.superSecureManagingOfficerCount = superSecureManagingOfficerCount;
    }

    public void setAccounts(IAccounts accounts) {
        this.accounts = (Accounts) accounts;
    }

    private static class Updated implements IUpdated {
        @Field("at")
        private Instant at;

        @Field("by")
        private String by;

        @Field("type")
        private String type;

        @Override
        public void setAt(Instant at) {
            this.at = at;
        }

        @Override
        public void setBy(String by) {
            this.by = by;
        }

        @Override
        public void setType(String type) {
            this.type = type;
        }
    }

    private static class ForeignCompanyDetails implements IForeignCompanyDetails {
        @Field("governed_by")
        private String governedBy;

        @Field("registration_number")
        private String registrationNumber;

        @Field("legal_form")
        private String legalForm;

        @Field("originating_registry")
        private OriginatingRegistry originatingRegistry;

        @Field("is_a_credit_financial_institution")
        private Boolean isACreditFinancialInstitution;

        @Field("business_activity")
        private String businessActivity;

        @Field("accounting_requirement")
        private AccountingRequirement accountingRequirement;

        @Field("accounts")
        private AccountsDetails accountsDetails;

        @Override
        public void setGovernedBy(String governedBy) {
            this.governedBy = governedBy;
        }

        @Override
        public String getLegalForm() {
            return this.legalForm;
        }

        @Override
        public void setLegalForm(String legalForm) {
            this.legalForm = legalForm;
        }

        @Override
        public void setOriginatingRegistry(IOriginatingRegistry originatingRegistry) {
            if (originatingRegistry instanceof OriginatingRegistry) {
                this.originatingRegistry = (OriginatingRegistry) originatingRegistry;
            }
        }

        @Override
        public void setCreditFinancialInstitution(Boolean creditFinancialInstitution) {
            this.isACreditFinancialInstitution = creditFinancialInstitution;
        }

        @Override
        public void setBusinessActivity(String businessActivity) {
            this.businessActivity = businessActivity;
        }

        @Override
        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        @Override
        public void setAccountingRequirement(IAccountingRequirement accountingRequirement) {
            if (accountingRequirement instanceof AccountingRequirement) {
                this.accountingRequirement = (AccountingRequirement) accountingRequirement;
            }
        }

        @Override
        public void setAccounts(IAccountsDetails accountsDetails) {
            if (accountsDetails instanceof AccountsDetails) {
                this.accountsDetails = (AccountsDetails) accountsDetails;
            }
        }
    }

    private static class OriginatingRegistry implements IOriginatingRegistry {
        @Field("country")
        private String country;

        @Field("name")
        private String name;

        @Override
        public void setCountry(String country) {
            this.country = country;
        }

        @Override
        public void setName(String name) {
            this.name = name;
        }
    }

    private static class AccountingRequirement implements IAccountingRequirement {
        @Field("foreign_account_type")
        private String foreignAccountType;

        @Field("terms_of_account_publication")
        private String termsOfAccountPublication;

        @Override
        public void setForeignAccountType(String foreignAccountType) {
            this.foreignAccountType = foreignAccountType;
        }

        @Override
        public void setTermsOfAccountPublication(String termsOfAccountPublication) {
            this.termsOfAccountPublication = termsOfAccountPublication;
        }
    }

    private static class AccountsDetails implements IAccountsDetails {
        @Field("account_period_from")
        private AccountPeriod accountPeriodFrom;

        @Field("account_period_to")
        private AccountPeriod accountPeriodTo;

        @Field("must_file_within")
        private MustFileWithin mustFileWithin;

        @Override
        public void setAccountPeriodFrom(String day, String month) {
            this.accountPeriodFrom = new AccountPeriod(day, month);
        }

        @Override
        public void setAccountPeriodTo(String day, String month) {
            this.accountPeriodTo = new AccountPeriod(day, month);
        }

        @Override
        public void setMustFileWithin(IMustFileWithin mustFileWithin) {
            if (mustFileWithin instanceof MustFileWithin) {
                this.mustFileWithin = (MustFileWithin) mustFileWithin;
            }
        }
    }

    private static class MustFileWithin implements IMustFileWithin {
        @Field("months")
        private int months;

        @Override
        public void setMonths(int months) {
            this.months = months;
        }

        public int getMonths() {
            return months;
        }
    }

    private static class AccountPeriod {
        @Field("day")
        private String day;

        @Field("month")
        private String month;

        public AccountPeriod(String day, String month) {
            this.day = day;
            this.month = month;
        }
    }

    private static class Accounts implements IAccounts {
        @Field("overdue")
        private Boolean overdue;

        @Field("next_made_up_to")
        private Instant nextMadeUpTo;

        @Field("next_due")
        private Instant nextDue;

        @Field("accounting_reference_date")
        private AccountingReferenceDate accountingReferenceDate;

        @Field("next_accounts")
        private NextAccounts nextAccounts;

        @Field("last_accounts")
        private LastAccounts lastAccounts;

        @Override
        public void setOverdue(Boolean overdue) {
            this.overdue = overdue;
        }

        @Override
        public void setNextMadeUpTo(Instant nextMadeUpTo) {
            this.nextMadeUpTo = nextMadeUpTo;
        }

        @Override
        public void setNextDue(Instant nextDue) {
            this.nextDue = nextDue;
        }

        @Override
        public void setAccountingReferenceDate(AccountingReferenceDate accountingReferenceDate) {
            this.accountingReferenceDate = accountingReferenceDate;
        }

        @Override
        public void setNextAccounts(NextAccounts nextAccounts) {
            this.nextAccounts = nextAccounts;
        }

        @Override
        public void setLastAccounts(LastAccounts lastAccounts) {
            this.lastAccounts = lastAccounts;
        }
    }

    public static class AccountingReferenceDate {
        @Field("day")
        private String day;

        @Field("month")
        private String month;

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }
    }

    public static class NextAccounts {
        @Field("overdue")
        private Boolean overdue;

        @Field("due_on")
        private Instant dueOn;

        @Field("period_start_on")
        private Instant periodStartOn;

        @Field("period_end_on")
        private Instant periodEndOn;

        public Boolean getOverdue() {
            return overdue;
        }

        public void setOverdue(Boolean overdue) {
            this.overdue = overdue;
        }

        public Instant getDueOn() {
            return dueOn;
        }

        public void setDueOn(Instant dueOn) {
            this.dueOn = dueOn;
        }

        public Instant getPeriodStartOn() {
            return periodStartOn;
        }

        public void setPeriodStartOn(Instant periodStartOn) {
            this.periodStartOn = periodStartOn;
        }

        public Instant getPeriodEndOn() {
            return periodEndOn;
        }

        public void setPeriodEndOn(Instant periodEndOn) {
            this.periodEndOn = periodEndOn;
        }
    }

    public static class LastAccounts {
        @Field("type")
        private String type;

        @Field("period_start_on")
        private Instant periodStartOn;

        @Field("period_end_on")
        private Instant periodEndOn;

        @Field("made_up_to")
        private Instant madeUpTo;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Instant getPeriodStartOn() {
            return periodStartOn;
        }

        public void setPeriodStartOn(Instant periodStartOn) {
            this.periodStartOn = periodStartOn;
        }

        public Instant getPeriodEndOn() {
            return periodEndOn;
        }

        public void setPeriodEndOn(Instant periodEndOn) {
            this.periodEndOn = periodEndOn;
        }

        public Instant getMadeUpTo() {
            return madeUpTo;
        }

        public void setMadeUpTo(Instant madeUpTo) {
            this.madeUpTo = madeUpTo;
        }
    }
}