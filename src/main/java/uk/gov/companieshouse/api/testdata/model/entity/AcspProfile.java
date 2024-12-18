package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "acsp_profile")
public class AcspProfile {

    @Id
    @Field("_id")
    private String id;

    @Field("version")
    private Long version;

    @Field("data")
    private Data data = new Data();

    @Field("sensitive_data")
    private SensitiveData sensitiveData;

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public Long getVersion() { return version; }

    public void setVersion(Long version) { this.version = version; }

    public Data getData() { return data; }

    public void setData(Data data) { this.data = data; }

    public SensitiveData getSensitiveData() { return sensitiveData; }

    public void setSensitiveData(SensitiveData sensitiveData) { this.sensitiveData = sensitiveData; }

    /**
     * Setter methods delegating to Data fields
     */
    public void setAcspNumber(String acspNumber) {
        this.data.setAcspNumber(acspNumber);
    }

    public void setCompanyName(String companyName) {
        this.data.setName(companyName);
    }

    public void setType(String type) {
        this.data.setType(type);
    }

    public void setStatus(String status) {
        this.data.setStatus(status);
    }

    public void setLinks(Links links) {
        this.data.setLinks(links);
    }

    public void setRegisteredOfficeAddress(Address address) {
        this.data.setRegisteredOfficeAddress(address);
    }

    public void setJurisdiction(String jurisdiction) {
        this.data.setJurisdiction(jurisdiction);
    }

    public static class Data {

        @Field("acsp_number")
        private String acspNumber;

        @Field("name")
        private String name;

        @Field("notified_from")
        private Instant notifiedFrom;

        @Field("status")
        private String status;

        @Field("type")
        private String type;

        @Field("business_sector")
        private String businessSector;

        @Field("etag")
        private String etag;

        @Field("registered_office_address")
        private Address registeredOfficeAddress;

        @Field("service_address")
        private Address serviceAddress;

        @Field("aml_details")
        private List<AmlDetail> amlDetails;

        @Field("links")
        private Links links;

        @Field("jurisdiction")
        private String jurisdiction;

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

        public Instant getNotifiedFrom() {
            return notifiedFrom;
        }

        public void setNotifiedFrom(Instant notifiedFrom) {
            this.notifiedFrom = notifiedFrom;
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

        public String getBusinessSector() {
            return businessSector;
        }

        public void setBusinessSector(String businessSector) {
            this.businessSector = businessSector;
        }

        public String getEtag() {
            return etag;
        }

        public void setEtag(String etag) {
            this.etag = etag;
        }

        public Address getRegisteredOfficeAddress() {
            return registeredOfficeAddress;
        }

        public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
            this.registeredOfficeAddress = registeredOfficeAddress;
        }

        public Address getServiceAddress() {
            return serviceAddress;
        }

        public void setServiceAddress(Address serviceAddress) {
            this.serviceAddress = serviceAddress;
        }

        public List<AmlDetail> getAmlDetails() {
            return amlDetails;
        }

        public void setAmlDetails(List<AmlDetail> amlDetails) {
            this.amlDetails = amlDetails;
        }

        public Links getLinks() {
            return links;
        }

        public void setLinks(Links links) {
            this.links = links;
        }

        public String getJurisdiction() {
            return jurisdiction;
        }

        public void setJurisdiction(String jurisdiction) {
            this.jurisdiction = jurisdiction;
        }
    }

    public static class Links {
        @Field("self")
        private String self;

        public String getSelf() {
            return self;
        }

        public void setSelf(String self) {
            this.self = self;
        }
    }

    public static class AmlDetail {
        @Field("supervisory_body")
        private String supervisoryBody;

        @Field("membership_details")
        private String membershipDetails;

        public String getSupervisoryBody() {
            return supervisoryBody;
        }

        public void setSupervisoryBody(String supervisoryBody) {
            this.supervisoryBody = supervisoryBody;
        }

        public String getMembershipDetails() {
            return membershipDetails;
        }

        public void setMembershipDetails(String membershipDetails) {
            this.membershipDetails = membershipDetails;
        }
    }

    public static class Address {
        @Field("care_of")
        private String careOf;
        @Field("address_line_1")
        private String addressLine1;
        @Field("address_line_2")
        private String addressLine2;
        @Field("country")
        private String country;
        @Field("locality")
        private String locality;
        @Field("po_box")
        private String poBox;
        @Field("postal_code")
        private String postalCode;
        @Field("premises")
        private String premises;
        @Field("region")
        private String region;

        public String getCareOf() { return careOf; }
        public void setCareOf(String careOf) { this.careOf = careOf; }

        public String getAddressLine1() { return addressLine1; }
        public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

        public String getAddressLine2() { return addressLine2; }
        public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }

        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }

        public String getLocality() { return locality; }
        public void setLocality(String locality) { this.locality = locality; }

        public String getPoBox() { return poBox; }
        public void setPoBox(String poBox) { this.poBox = poBox; }

        public String getPostalCode() { return postalCode; }
        public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

        public String getPremises() { return premises; }
        public void setPremises(String premises) { this.premises = premises; }

        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
    }

    public static class SensitiveData {
        @Field("email")
        private String email;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}
