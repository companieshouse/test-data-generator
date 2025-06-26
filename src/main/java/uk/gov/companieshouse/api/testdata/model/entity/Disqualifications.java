package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "disqualifications")
public class Disqualifications {

    @Id
    @Field("_id")
    private String id;

    @Field("data.company_number")
    private String companyNumber;

    @Field("data.person_number")
    private Long personNumber;

    @Field("data.country_of_registration")
    private String countryOfRegistration;

    @Field("data.etag")
    private String etag;

    @Field("data.name")
    private String name;

    @Field("data.date_of_birth")
    private Date dateOfBirth;

    @Field("data.links.self")
    private String linksSelf;

    @Field("data.disqualifications.address")
    private Address address;

    @Field("data.disqualifications.case_identifier")
    private String disqCaseIdentifier;

    @Field("data.disqualifications.company_names")
    private List<String> disqCompanyNames;

    @Field("data.disqualifications.court_name")
    private String disqCourtName;

    @Field("data.disqualifications.disqualification_type")
    private String disqDisqualificationType;

    @Field("data.disqualifications.disqualified_from")
    private Instant disqDisqualifiedFrom;

    @Field("data.disqualifications.disqualified_until")
    private Instant disqDisqualifiedUntil;

    @Field("data.disqualifications.heard_on")
    private Instant disqHeardOn;

    @Field("data.disqualifications.last_variation.varied_on")
    private Instant disqLastVariationVariedOn;

    @Field("data.disqualifications.last_variation.case_identifier")
    private String disqLastVariationCaseIdentifier;

    @Field("data.disqualifications.last_variation.court_name")
    private String disqLastVariationCourtName;

    @Field("data.disqualifications.reason.act")
    private String disqReasonAct;

    @Field("data.disqualifications.reason.description_identifier")
    private String disqReasonDescriptionIdentifier;

    @Field("data.disqualifications.reason.article")
    private String disqReasonArticle;

    @Field("data.permissions_to_act.company_names")
    private List<String> ptaCompanyNames;

    @Field("data.permissions_to_act.court_name")
    private String ptaCourtName;

    @Field("data.permissions_to_act.expires_on")
    private Instant ptaExpiresOn;

    @Field("data.permissions_to_act.granted_on")
    private Instant ptaGrantedOn;

    @Field("data.permissions_to_act.purpose")
    private String ptaPurpose;

    @Field("officer_disq_id")
    private String officerDisqId;

    @Field("officer_detail_id")
    private String officerDetailId;

    @Field("officer_id_raw")
    private String officerIdRaw;

    @Field("created.at")
    private Instant createdAt;

    @Field("delta_at")
    private String deltaAt;

    @Field("is_corporate_officer")
    private Boolean isCorporateOfficer;

    @Field("updated.at")
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public void setPersonNumber(Long personNumber) {
        this.personNumber = personNumber;
    }

    public void setCountryOfRegistration(String countryOfRegistration) {
        this.countryOfRegistration = countryOfRegistration;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public void setLinksSelf(String linksSelf) {
        this.linksSelf = linksSelf;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setDisqCaseIdentifier(String disqCaseIdentifier) {
        this.disqCaseIdentifier = disqCaseIdentifier;
    }

    public void setDisqCompanyNames(List<String> disqCompanyNames) {
        this.disqCompanyNames = disqCompanyNames;
    }

    public void setDisqCourtName(String disqCourtName) {
        this.disqCourtName = disqCourtName;
    }

    public void setDisqDisqualificationType(String disqDisqualificationType) {
        this.disqDisqualificationType = disqDisqualificationType;
    }

    public void setDisqDisqualifiedFrom(Instant disqDisqualifiedFrom) {
        this.disqDisqualifiedFrom = disqDisqualifiedFrom;
    }

    public void setDisqDisqualifiedUntil(Instant disqDisqualifiedUntil) {
        this.disqDisqualifiedUntil = disqDisqualifiedUntil;
    }

    public void setDisqHeardOn(Instant disqHeardOn) {
        this.disqHeardOn = disqHeardOn;
    }

    public void setDisqLastVariationVariedOn(Instant disqLastVariationVariedOn) {
        this.disqLastVariationVariedOn = disqLastVariationVariedOn;
    }

    public void setDisqLastVariationCaseIdentifier(String disqLastVariationCaseIdentifier) {
        this.disqLastVariationCaseIdentifier = disqLastVariationCaseIdentifier;
    }

    public void setDisqLastVariationCourtName(String disqLastVariationCourtName) {
        this.disqLastVariationCourtName = disqLastVariationCourtName;
    }

    public void setDisqReasonAct(String disqReasonAct) {
        this.disqReasonAct = disqReasonAct;
    }

    public void setDisqReasonDescriptionIdentifier(String disqReasonDescriptionIdentifier) {
        this.disqReasonDescriptionIdentifier = disqReasonDescriptionIdentifier;
    }

    public void setDisqReasonArticle(String disqReasonArticle) {
        this.disqReasonArticle = disqReasonArticle;
    }

    public void setPtaCompanyNames(List<String> ptaCompanyNames) {
        this.ptaCompanyNames = ptaCompanyNames;
    }

    public void setPtaCourtName(String ptaCourtName) {
        this.ptaCourtName = ptaCourtName;
    }

    public void setPtaExpiresOn(Instant ptaExpiresOn) {
        this.ptaExpiresOn = ptaExpiresOn;
    }

    public void setPtaGrantedOn(Instant ptaGrantedOn) {
        this.ptaGrantedOn = ptaGrantedOn;
    }

    public void setPtaPurpose(String ptaPurpose) {
        this.ptaPurpose = ptaPurpose;
    }

    public void setOfficerDisqId(String officerDisqId) {
        this.officerDisqId = officerDisqId;
    }

    public void setOfficerDetailId(String officerDetailId) {
        this.officerDetailId = officerDetailId;
    }

    public void setOfficerIdRaw(String officerIdRaw) {
        this.officerIdRaw = officerIdRaw;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void setDeltaAt(String deltaAt) {
        this.deltaAt = deltaAt;
    }

    public void setIsCorporateOfficer(Boolean corporateOfficer) {
        isCorporateOfficer = corporateOfficer;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}