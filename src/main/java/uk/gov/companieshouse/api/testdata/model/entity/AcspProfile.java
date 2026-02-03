package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
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

    @Field("data.notified_from")
    private Instant notifiedFrom;

    @Field("data.name")
    private String name;

    @Field("data.status")
    private String status;

    @Field("data.type")
    private String type;

    @Field("data.business_sector")
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

    @Field("sensitive_data.email")
    private String email;

    @Field("created")
    private AuditDetails created;

    @Field("updated")
    private AuditDetails updated;

    @Field("delta_at")
    private String deltaAt;

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public SoleTraderDetails getSoleTraderDetails() {
        return soleTraderDetails;
    }

    public void setSoleTraderDetails(SoleTraderDetails soleTraderDetails) {
        this.soleTraderDetails = soleTraderDetails;
    }

    public void setRegisteredOfficeAddress(Address registeredOfficeAddress) {
        this.registeredOfficeAddress = registeredOfficeAddress;
    }

    public Address getRegisteredOfficeAddress() { return registeredOfficeAddress; }

    public void setServiceAddress(Address serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public Address getServiceAddress() { return serviceAddress; }

    public void setBusinessSector(String businessSector) {
        this.businessSector = businessSector;
    }

    public String getBusinessSector() {
        return businessSector;
    }

    public Instant getNotifiedFrom() { return notifiedFrom; }

    public void setNotifiedFrom(Instant notifiedFrom) { this.notifiedFrom = notifiedFrom; }

    public AuditDetails getCreated() {
        return created;
    }

    public void setCreated(AuditDetails created) { this.created = created; }

    public AuditDetails getUpdated() {
        return updated;
    }

    public void setUpdated(AuditDetails updated) { this.updated = updated; }

    public String getDeltaAt() { return deltaAt; }

    public void setDeltaAt(String deltaAt) { this.deltaAt = deltaAt; }
}