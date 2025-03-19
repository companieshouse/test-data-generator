package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "acsp_profile")
public class AcspProfile {

    @Id
    @Field("_id")
    private String id;

    @Field("version")
    private Long version;

    @Field("data.acsp_number")
    private String acspNumber;

    @Field("data.name")
    private String name;

    @Field("data.status")
    private String status;

    @Field("data.type")
    private String type;

    @Field("data.business_Sector")
    private String businessSector;

    @Field("data.etag")
    private String etag;

    @Field("data.links.self")
    private String linksSelf;

    @Field("data.aml_details")
    private List<AmlDetails> amlDetails;

    @Field("data.sole_trader_details")
    private SoleTraderDetails soleTraderDetails;

    @Field("data.registered_office_address")
    private Address registeredOfficeAddress;

    @Field("data.service_address")
    private Address serviceAddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public void setLinksSelf(String linksSelf) {
        this.linksSelf = linksSelf;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public List<AmlDetails> getAmlDetails() {
        return amlDetails;
    }

    public void setAmlDetails(List<AmlDetails> amlDetails) {
        this.amlDetails = amlDetails;
    }

    public ISoleTraderDetails getSoleTraderDetails() {
        return soleTraderDetails;
    }

    public void setSoleTraderDetails(ISoleTraderDetails soleTraderDetails) {
        this.soleTraderDetails = (SoleTraderDetails) soleTraderDetails;
    }

    public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = (Address) registeredOfficeAddress;
    }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = (Address) serviceAddress;
    }

    public static ISoleTraderDetails createSoleTraderDetails() {
        return new SoleTraderDetails();
    }

    public static Address createAddress() {
        return new Address();
    }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public static interface ISoleTraderDetails {
        void setForename(String forename);

        void setSurname(String surname);

        void setNationality(String nationality);

        void setUsualResidentialCountry(String usualResidentialCountry);

        String getForename();

        String getSurname();

        String getNationality();

        String getUsualResidentialCountry();
    }

    public static interface ISensitiveData {
        void setEmail(String email);
    }

    private static class SoleTraderDetails implements ISoleTraderDetails {
        @Field("forename")
        private String forename;
        @Field("surname")
        private String surname;
        @Field("nationality")
        private String nationality;
        @Field("usual_residential_country")
        private String usualResidentialCountry;

        @Override
        public void setForename(String forename) {
            this.forename = forename;
        }

        @Override
        public void setSurname(String surname) {
            this.surname = surname;
        }

        @Override
        public void setNationality(String nationality) {
            this.nationality = nationality;
        }

        @Override
        public void setUsualResidentialCountry(String usualResidentialCountry) {
            this.usualResidentialCountry = usualResidentialCountry;
        }

        @Override
        public String getForename() {
            return forename;
        }

        @Override
        public String getSurname() {
            return surname;
        }

        @Override
        public String getNationality() {
            return nationality;
        }

        @Override
        public String getUsualResidentialCountry() {
            return usualResidentialCountry;
        }
    }
}