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
    @Field("data.name_elements.middle_name")
    private String middleName;
    @Field("data.name_elements.forename")
    private String forename;
    @Field("data.name_elements.title")
    private String title;
    @Field("data.name_elements.surname")
    private String surname;
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
    @Field("data.country_of_residence")
    private String countryOfResidence;
    @Field("data.date_of_birth")
    private Instant dateOfBirth;
    @Field("data.links.self")
    private Links self;
    @Field("data.etag")
    private String etag;
    @Field("company_number")
    private String companyNumber;
    @Field("psc_id")
    private  String pscId;
    @Field("updated.at")
    private Instant updatedAt;
    @Field("notification_id")
    private String notificationId;
    @Field("created.at")
    private Instant createdAt;

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

    public String getMiddleName() { return middleName; }

    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getForename() { return forename; }

    public void setForename(String forename) { this.forename = forename; }

    public String getTitle() { return title; }

    public void setTitle(String title) { this.title = title; }

    public String getSurname() { return surname; }

    public void setSurname(String surname) { this.surname = surname; }

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

    public String getCountryOfResidence() { return countryOfResidence; }

    public void setCountryOfResidence(String countryOfResidence) { this.countryOfResidence = countryOfResidence; }

    public Instant getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(Instant dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Links getSelf() { return self; }

    public void setSelf(Links self) { this.self = self; }

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
}
