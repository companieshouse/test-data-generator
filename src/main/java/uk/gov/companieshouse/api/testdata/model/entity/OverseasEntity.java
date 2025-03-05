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

    public static interface IUpdated {
        void setAt(Instant at);

        void setBy(String by);

        void getType(String type);

        void setType(String type);
    }

    public static interface IForeignCompanyDetails {
        void setGovernedBy(String governedBy);

        void setLegalForm(String legalForm);

        void setOriginatingRegistry(IOriginatingRegistry originatingRegistry);
    }

    public static interface IOriginatingRegistry {
        void setCountry(String country);

        void setName(String name);
    }

    public static IForeignCompanyDetails createForeignCompanyDetails() {
        return new ForeignCompanyDetails();
    }

    public static IOriginatingRegistry createOriginatingRegistry() {
        return new OriginatingRegistry();
    }

    public static IUpdated createUpdated() {
        return new Updated();
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setHasMortgages(Boolean hasMortgages) {
        this.hasMortgages = hasMortgages;
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

    public void setForeignCompanyDetails(IForeignCompanyDetails foreignCompanyDetails) {
        this.foreignCompanyDetails = (ForeignCompanyDetails) foreignCompanyDetails;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void setSuperSecureManagingOfficerCount(Integer superSecureManagingOfficerCount) {
        this.superSecureManagingOfficerCount = superSecureManagingOfficerCount;
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
        public void getType(String type) {
            this.type = type;
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

        @Override
        public void setGovernedBy(String governedBy) {
            this.governedBy = governedBy;
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
}