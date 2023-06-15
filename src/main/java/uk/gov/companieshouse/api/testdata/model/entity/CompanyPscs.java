package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.List;

@Document(collection = "company_pscs")
public class CompanyPscs{

    @Id
    @Field("_id")
    private String id;
    @Field("delta_at")
    private String deltaAt;
    @Field("data.natures_of_control")
    private List<String> naturesOfControl;
    @Field("data.kind")
    private String kind;
    @Field("data.name_elements")
    private NameElements nameElements;
    @Field("data.name")
    private String name;
    @Field("data.notified_on")
    private Instant notifiedOn;
    @Field("data.nationality")
    private String nationality;
    @Field("data.address.postal_code")
    private String postalCode;
    @Field("data.address.premises")
    private String premises;
    @Field("data.address.locality")
    private String locality;
    @Field("data.address.country")
    private String country;
    @Field("data.address.address_line_1")
    private String addressLine1;
    @Field("data.address.address_line_2")
    private String addressLine2;
    @Field("data.address.care_of")
    private String careOf;
    @Field("data.address.poBox")
    private String poBox;
    @Field("data.address.region")
    private String region;
    @Field("data.country_of_residence")
    private String countryOfResidence;
    @Field("data.address_same_as_registered_office_address")
    private Boolean addressSameAsRegisteredOfficeAddress;
    @Field("data.ceased_on")
    private Instant ceasedOn;
    @Field("data.reference_etag")
    private String referenceEtag;
    @Field("data.reference_psc_id")
    private String referencePscId;
    @Field("data.register_entry_date")
    private Instant registerEntryDate;
    @Field("data.statement_action_date")
    private Instant statementActionDate;
    @Field("data.statement_type")
    private String statementType;
    @Field("data.date_of_birth")
    private Instant dateOfBirth;
    @Field("data.links")
    private Links links;
    @Field("data.etag")
    private String etag;
    @Field("company_number")
    private String companyNumber;
    @Field("psc_id")
    private String pscId;
    @Field("updated.at")
    private Instant updatedAt;
    @Field("notification_id")
    private String notificationId;
    @Field("created.at")
    private Instant createdAt;
    @Field("data.identification")
    private Identification identification;

    public String getId() {
        return id;
    }

    public void setId(String id) { this.id = id; }

    public String getDeltaAt() { return deltaAt; }

    public void setDeltaAt(String deltaAt) {this.deltaAt = deltaAt; }

    public List<String> getNaturesOfControl() { return naturesOfControl; }

    public void setNaturesOfControl(List<String> naturesOfControl) { this.naturesOfControl = naturesOfControl; }

    public String getKind() { return kind; }

    public void setKind(String kind) { this.kind = kind; }

    public NameElements getNameElements() {
        return nameElements;
    }

    public void setNameElements(NameElements nameElements) {
        this.nameElements = nameElements;
    }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public Instant getNotifiedOn() { return notifiedOn; }

    public void setNotifiedOn(Instant notifiedOn) { this.notifiedOn = notifiedOn; }

    public String getNationality() { return nationality; }

    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getPostalCode() { return postalCode; }

    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getPremises() { return premises; }

    public void setPremises(String premises) { this.premises = premises; }

    public String getLocality() { return locality; }

    public void setLocality(String locality) { this.locality = locality; }

    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }

    public String getAddressLine1() { return addressLine1; }

    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCareOf() {
        return careOf;
    }

    public void setCareOf(String careOf) {
        this.careOf = careOf;
    }

    public String getPoBox() {
        return poBox;
    }

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public Boolean getAddressSameAsRegisteredOfficeAddress() {
        return addressSameAsRegisteredOfficeAddress;
    }

    public void setAddressSameAsRegisteredOfficeAddress(Boolean addressSameAsRegisteredOfficeAddress) {
        this.addressSameAsRegisteredOfficeAddress = addressSameAsRegisteredOfficeAddress;
    }

    public Instant getCeasedOn() {
        return ceasedOn;
    }

    public void setCeasedOn(Instant ceasedOn) {
        this.ceasedOn = ceasedOn;
    }

    public String getReferenceEtag() {
        return referenceEtag;
    }

    public void setReferenceEtag(String referenceEtag) {
        this.referenceEtag = referenceEtag;
    }

    public String getReferencePscId() {
        return referencePscId;
    }

    public void setReferencePscId(String referencePscId) {
        this.referencePscId = referencePscId;
    }

    public Instant getRegisterEntryDate() {
        return registerEntryDate;
    }

    public void setRegisterEntryDate(Instant registerEntryDate) {
        this.registerEntryDate = registerEntryDate;
    }

    public Instant getStatementActionDate() {
        return statementActionDate;
    }

    public void setStatementActionDate(Instant statementActionDate) {
        this.statementActionDate = statementActionDate;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getCountryOfResidence() { return countryOfResidence; }

    public void setCountryOfResidence(String countryOfResidence) { this.countryOfResidence = countryOfResidence; }

    public Instant getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(Instant dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Links getLinks() { return links; }

    public void setLinks(Links links) { this.links = links; }

    public String getEtag() { return etag; }

    public void setEtag(String etag) { this.etag = etag; }

    public String getCompanyNumber() { return companyNumber; }

    public void setCompanyNumber(String companyNumber) { this.companyNumber = companyNumber; }

    public String getPscId() { return pscId; }

    public void setPscId(String pscId) { this.pscId = pscId; }

    public Instant getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }

    public String getNotificationId() { return notificationId; }

    public void setNotificationId(String notificationId) { this.notificationId = notificationId; }

    public Instant getCreatedAt() { return createdAt; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Identification getIdentification() {
        return identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }
}
