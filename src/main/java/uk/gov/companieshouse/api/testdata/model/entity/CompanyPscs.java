package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "delta_company_pscs")
public class CompanyPscs {

    @Id
    @Field("_id")
    private String id;
    @Field("psc_id")
    private String pscId;
    @Field("delta_at")
    private String deltaAt;
    @Field("notification_id")
    private String notificationId;
    @Field("company_number")
    private String companyNumber;
    @Field("created.at")
    private Instant createdAt;
    @Field("updated.at")
    private Instant updatedAt;
    @Field("data.sanctioned")
    private Boolean sanctioned;
    @Field("data.etag")
    private String etag;
    @Field("data.name_elements")
    private NameElements nameElements;
    @Field("data.name")
    private String name;
    @Field("data.kind")
    private String kind;
    @Field("data.notified_on")
    private Instant notifiedOn;
    @Field("data.service_address_is_same_as_registered_office_address")
    private Boolean serviceAddressSameAsRegisteredOfficeAddress;
    @Field("data.is_sanctioned")
    private Boolean isSanctioned;
    @Field("data.natures_of_control")
    private List<String> naturesOfControl;
    @Field("data.links")
    private Links links;
    @Field("data.description")
    private String description;
    @Field("data.nationality")
    private String nationality;
    @Field("data.name_elements.title")
    private String nameTitle;
    @Field("data.name_elements.forename")
    private String nameForename;
    @Field("data.name_elements.other_forenames")
    private String nameOtherForenames;
    @Field("data.name_elements.surname")
    private String nameSurname;
    @Field("data.country_of_residence")
    private String countryOfResidence;
    @Field("data.principal_office_address")
    private Address principalOfficeAddress;
    @Field("data.address")
    private Address address;
    @Field("data.identification")
    private Identification identification;
    @Field("sensitive_data.usual_residential_address")
    private Address usualResidentialAddress;
    @Field("sensitive_data.date_of_birth")
    private DateOfBirth dateOfBirth;
    @Field("sensitive_data.residential_address_is_same_as_service_address")
    private Boolean residentialAddressSameAsServiceAddress;
    @Field("data.address_same_as_registered_office_address")
    private Boolean addressSameAsRegisteredOfficeAddress;
    @Field("data.ceased_on")
    private Instant ceasedOn;
    @Field("data.ceased")
    private Boolean ceased;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPscId() {
        return pscId;
    }

    public void setPscId(String pscId) {
        this.pscId = pscId;
    }

    public String getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(String deltaAt) {
        this.deltaAt = deltaAt;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getSanctioned() {
        return sanctioned;
    }

    public void setSanctioned(Boolean sanctioned) {
        this.sanctioned = sanctioned;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public NameElements getNameElements() {
        return nameElements;
    }

    public void setNameElements(NameElements nameElements) {
        this.nameElements = nameElements;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public void setNotifiedOn(Instant notifiedOn) {
        this.notifiedOn = notifiedOn;
    }

    public void setServiceAddressSameAsRegisteredOfficeAddress(
            Boolean serviceAddressSameAsRegisteredOfficeAddress) {
        this.serviceAddressSameAsRegisteredOfficeAddress =
                serviceAddressSameAsRegisteredOfficeAddress;
    }

    public void setIsSanctioned(Boolean isSanctioned) {
        this.isSanctioned = isSanctioned;
    }

    public void setNaturesOfControl(List<String> naturesOfControl) {
        this.naturesOfControl = naturesOfControl;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public void setPrincipalOfficeAddress(Address principalOfficeAddress) {
        this.principalOfficeAddress = principalOfficeAddress;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setUsualResidentialAddress(Address usualResidentialAddress) {
        this.usualResidentialAddress = usualResidentialAddress;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setNameTitle(String nameTitle) {
        this.nameTitle = nameTitle;
    }

    public void setNameForename(String nameForename) {
        this.nameForename = nameForename;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }

    public void setCountryOfResidence(String countryOfResidence) {
        this.countryOfResidence = countryOfResidence;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    public void setDateOfBirth(DateOfBirth dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setResidentialAddressSameAsServiceAddress(
            Boolean residentialAddressSameAsServiceAddress) {
        this.residentialAddressSameAsServiceAddress = residentialAddressSameAsServiceAddress;
    }

    public void setAddressSameAsRegisteredOfficeAddress(
            Boolean addressSameAsRegisteredOfficeAddress) {
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

    public void setStatementActionDate(Instant statementActionDate) {
        this.statementActionDate = statementActionDate;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public void setCeased(Boolean ceased) {
        this.ceased = ceased;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}