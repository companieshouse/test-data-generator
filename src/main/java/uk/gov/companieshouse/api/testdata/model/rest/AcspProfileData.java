package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.AmlDetails;
import uk.gov.companieshouse.api.testdata.model.entity.SoleTraderDetails;
import uk.gov.companieshouse.api.testdata.model.entity.AuditDetails;

import java.time.Instant;
import java.util.List;

public class AcspProfileData {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("acsp_number")
    private final String acspNumber;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("business_sector")
    private final String businessSector;

    @JsonProperty("etag")
    private final String etag;

    @JsonProperty("links_self")
    private final String linksSelf;

    @JsonProperty("aml_details")
    private final List<AmlDetails> amlDetails;

    @JsonProperty("sole_trader_details")
    private final SoleTraderDetails soleTraderDetails;

    @JsonProperty("registered_office_address")
    private final Address registeredOfficeAddress;

    @JsonProperty("service_address")
    private final Address serviceAddress;

    @JsonProperty("email")
    private final String email;

    @JsonProperty("notified_from")
    private final Instant notifiedFrom;

    @JsonProperty("created")
    private final AuditDetails created;

    @JsonProperty("updated")
    private final AuditDetails updated;

    @JsonProperty("delta_at")
    private final String deltaAt;

    @JsonProperty("version")
    private final Long version;

    public AcspProfileData(AcspProfile profile) {
        this.id = profile.getId();
        this.acspNumber = profile.getAcspNumber();
        this.name = profile.getName();
        this.status = profile.getStatus();
        this.type = profile.getType();
        this.businessSector = profile.getBusinessSector();
        this.etag = profile.getEtag();
        this.linksSelf = profile.getLinksSelf();
        this.amlDetails = profile.getAmlDetails();
        this.soleTraderDetails = profile.getSoleTraderDetails();
        this.registeredOfficeAddress = profile.getRegisteredOfficeAddress();
        this.serviceAddress = profile.getServiceAddress();
        this.email = profile.getEmail();
        this.notifiedFrom = profile.getNotifiedFrom();
        this.created = profile.getCreated();
        this.updated = profile.getUpdated();
        this.deltaAt = profile.getDeltaAt();
        this.version = profile.getVersion();
    }

    public String getId() {
        return id;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getEtag() {
        return etag;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public String getEmail() {
        return email;
    }

    public AuditDetails getCreated() {
        return created;
    }

    public AuditDetails getUpdated() {
        return updated;
    }

}
