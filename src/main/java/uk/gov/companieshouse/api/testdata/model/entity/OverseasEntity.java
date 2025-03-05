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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getHasMortgages() {
        return hasMortgages;
    }

    public void setHasMortgages(Boolean hasMortgages) {
        this.hasMortgages = hasMortgages;
    }

    public Instant getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(Instant deltaAt) {
        this.deltaAt = deltaAt;
    }

    public Updated getUpdated() {
        return updated;
    }

    public void setUpdated(Updated updated) {
        this.updated = updated;
    }

    public String getExternalRegistrationNumber() {
        return externalRegistrationNumber;
    }

    public void setExternalRegistrationNumber(String externalRegistrationNumber) {
        this.externalRegistrationNumber = externalRegistrationNumber;
    }

    public ForeignCompanyDetails getForeignCompanyDetails() {
        return foreignCompanyDetails;
    }

    public void setForeignCompanyDetails(ForeignCompanyDetails foreignCompanyDetails) {
        this.foreignCompanyDetails = foreignCompanyDetails;
    }

    public Address getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Integer getSuperSecureManagingOfficerCount() {
        return superSecureManagingOfficerCount;
    }

    public void setSuperSecureManagingOfficerCount(Integer superSecureManagingOfficerCount) {
        this.superSecureManagingOfficerCount = superSecureManagingOfficerCount;
    }

    public class Updated {
        @Field("at")
        private Instant at;

        @Field("by")
        private String by;

        @Field("type")
        private String type;

        public Instant getAt() {
            return at;
        }

        public void setAt(Instant at) {
            this.at = at;
        }

        public String getBy() {
            return by;
        }

        public void setBy(String by) {
            this.by = by;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public class ForeignCompanyDetails {
        @Field("accounting_requirement")
        private AccountingRequirement accountingRequirement;

        @Field("originating_registry")
        private OriginatingRegistry originatingRegistry;

        @Field("governed_by")
        private String governedBy;

        @Field("registration_number")
        private String registrationNumber;

        @Field("is_a_credit_financial_institution")
        private Boolean isACreditFinancialInstitution;

        @Field("legal_form")
        private String legalForm;

        @Field("accounts")
        private Accounts accounts;

        @Field("business_activity")
        private String businessActivity;

        public AccountingRequirement getAccountingRequirement() {
            return accountingRequirement;
        }

        public void setAccountingRequirement(AccountingRequirement accountingRequirement) {
            this.accountingRequirement = accountingRequirement;
        }

        public OriginatingRegistry getOriginatingRegistry() {
            return originatingRegistry;
        }

        public void setOriginatingRegistry(OriginatingRegistry originatingRegistry) {
            this.originatingRegistry = originatingRegistry;
        }

        public String getGovernedBy() {
            return governedBy;
        }

        public void setGovernedBy(String governedBy) {
            this.governedBy = governedBy;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public void setRegistrationNumber(String registrationNumber) {
            this.registrationNumber = registrationNumber;
        }

        public Boolean getIsACreditFinancialInstitution() {
            return isACreditFinancialInstitution;
        }

        public void setIsACreditFinancialInstitution(Boolean isACreditFinancialInstitution) {
            this.isACreditFinancialInstitution = isACreditFinancialInstitution;
        }

        public String getLegalForm() {
            return legalForm;
        }

        public void setLegalForm(String legalForm) {
            this.legalForm = legalForm;
        }

        public Accounts getAccounts() {
            return accounts;
        }

        public void setAccounts(Accounts accounts) {
            this.accounts = accounts;
        }

        public String getBusinessActivity() {
            return businessActivity;
        }

        public void setBusinessActivity(String businessActivity) {
            this.businessActivity = businessActivity;
        }

        public class OriginatingRegistry {
            @Field("country")
            private String country;

            @Field("name")
            private String name;

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }

        public class AccountingRequirement {
            @Field("terms_of_account_publication")
            private String termsOfAccountPublication;

            @Field("foreign_account_type")
            private String foreignAccountType;

            public String getTermsOfAccountPublication() {
                return termsOfAccountPublication;
            }

            public void setTermsOfAccountPublication(String termsOfAccountPublication) {
                this.termsOfAccountPublication = termsOfAccountPublication;
            }

            public String getForeignAccountType() {
                return foreignAccountType;
            }

            public void setForeignAccountType(String foreignAccountType) {
                this.foreignAccountType = foreignAccountType;
            }
        }

        public class Accounts {
            @Field("must_file_within")
            private MustFileWithin mustFileWithin;

            @Field("account_period_to")
            private AccountPeriod accountPeriodTo;

            @Field("account_period_from")
            private AccountPeriod accountPeriodFrom;

            public MustFileWithin getMustFileWithin() {
                return mustFileWithin;
            }

            public void setMustFileWithin(MustFileWithin mustFileWithin) {
                this.mustFileWithin = mustFileWithin;
            }

            public AccountPeriod getAccountPeriodTo() {
                return accountPeriodTo;
            }

            public void setAccountPeriodTo(AccountPeriod accountPeriodTo) {
                this.accountPeriodTo = accountPeriodTo;
            }

            public AccountPeriod getAccountPeriodFrom() {
                return accountPeriodFrom;
            }

            public void setAccountPeriodFrom(AccountPeriod accountPeriodFrom) {
                this.accountPeriodFrom = accountPeriodFrom;
            }

            public class MustFileWithin {
                @Field("months")
                private String months;

                public String getMonths() {
                    return months;
                }

                public void setMonths(String months) {
                    this.months = months;
                }
            }

            public class AccountPeriod {
                @Field("month")
                private String month;
                @Field("day")
                private String day;

                public String getMonth() {
                    return month;
                }

                public void setMonth(String month) {
                    this.month = month;
                }

                public String getDay() {
                    return day;
                }

                public void setDay(String day) {
                    this.day = day;
                }
            }
        }
    }
}