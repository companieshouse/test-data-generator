package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
import org.springframework.data.mongodb.core.mapping.Field;

public class ItemOptions {
    @Field("certificate_type")
    private String certificateType;

    @Field("collection_location")
    private String collectionLocation;

    @Field("contact_number")
    private String contactNumber;

    @Field("delivery_method")
    private String deliveryMethod;

    @Field("delivery_timescale")
    private String deliveryTimescale;

    @Field("include_company_objects_information")
    private Boolean includeCompanyObjectsInformation;

    @Field("include_good_standing_information")
    private Boolean includeGoodStandingInformation;

    @Field("include_email_copy")
    private Boolean includeEmailCopy;

    @Field("company_type")
    private String companyType;

    @Field("company_status")
    private String companyStatus;

    @Field("filing_history_documents")
    private List<FilingHistoryDocument> filingHistoryDocuments;

    @Field("director_details")
    private DirectorDetails directorDetails;

    @Field("forename")
    private String foreName;

    @Field("surname")
    private String surName;

    @Field("filing_history_date")
    private String filingHistoryDate;

    @Field("filing_history_description")
    private String filingHistoryDescription;

    @Field("filing_history_description_values")
    private FilingHistoryDescriptionValues filingHistoryDescriptionValues;

    @Field("filing_history_id")
    private String filingHistoryId;

    @Field("filing_history_type")
    private String filingHistoryType;

    @Field("filing_history_category")
    private String filingHistoryCategory;

    @Field("filing_history_barcode")
    private String filingHistoryBarcode;

    @Field("registered_office_address_details")
    private RegisteredOfficeAddressDetails registeredOfficeAddressDetails;

    @Field("secretary_details")
    private SecretaryDetails secretaryDetails;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getDeliveryTimescale() {
        return  deliveryTimescale;
    }

    public void setDeliveryTimescale(String deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
    }

    public Boolean getIncludeCompanyObjectsInformation() {
        return includeCompanyObjectsInformation;
    }

    public void setIncludeCompanyObjectsInformation(Boolean includeCompanyObjectsInformation) {
        this.includeCompanyObjectsInformation = includeCompanyObjectsInformation;
    }

    public Boolean getIncludeGoodStandingInformation() {
        return includeGoodStandingInformation;
    }

    public void setIncludeGoodStandingInformation(Boolean includeGoodStandingInformation) {
        this.includeGoodStandingInformation = includeGoodStandingInformation;
    }

    public boolean getIncludeEmailCopy() {
        return includeEmailCopy;
    }

    public void setIncludeEmailCopy(Boolean includeEmailCopy) {
        this.includeEmailCopy = includeEmailCopy;
    }

    public String getCompanyType() {
        return  companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getCompanyStatus() {
        return  companyStatus;
    }

    public void setCompanyStatus(String companyStatus) {
        this.companyStatus = companyStatus;
    }

    public List<FilingHistoryDocument> getFilingHistoryDocuments() {
        return filingHistoryDocuments;
    }

    public void setFilingHistoryDocuments(List<FilingHistoryDocument> filingHistoryDocuments) {
        this.filingHistoryDocuments = filingHistoryDocuments;
    }

    public String getCollectionLocation() {
        return collectionLocation;
    }

    public void setCollectionLocation(String collectionLocation) {
        this.collectionLocation = collectionLocation;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public DirectorDetails getDirectorDetails() {
        return directorDetails;
    }

    public void setDirectorDetails(DirectorDetails directorDetails) {
        this.directorDetails = directorDetails;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFilingHistoryDate() {
        return filingHistoryDate;
    }

    public void setFilingHistoryDate(String filingHistoryDate) {
        this.filingHistoryDate = filingHistoryDate;
    }

    public String getFilingHistoryDescription() {
        return filingHistoryDescription;
    }

    public void setFilingHistoryDescription(String filingHistoryDescription) {
        this.filingHistoryDescription = filingHistoryDescription;
    }

    public FilingHistoryDescriptionValues getFilingHistoryDescriptionValues() {
        return filingHistoryDescriptionValues;
    }

    public void setFilingHistoryDescriptionValues(FilingHistoryDescriptionValues filingHistoryDescriptionValues) {
        this.filingHistoryDescriptionValues = filingHistoryDescriptionValues;
    }

    public String getFilingHistoryId() {
        return filingHistoryId;
    }

    public void setFilingHistoryId(String filingHistoryId) {
        this.filingHistoryId = filingHistoryId;
    }

    public String getFilingHistoryType() {
        return filingHistoryType;
    }

    public void setFilingHistoryType(String filingHistoryType) {
        this.filingHistoryType = filingHistoryType;
    }

    public String getFilingHistoryCategory() {
        return filingHistoryCategory;
    }

    public void setFilingHistoryCategory(String filingHistoryCategory) {
        this.filingHistoryCategory = filingHistoryCategory;
    }

    public String getFilingHistoryBarcode() {
        return filingHistoryBarcode;
    }

    public void setFilingHistoryBarcode(String filingHistoryBarcode) {
        this.filingHistoryBarcode = filingHistoryBarcode;
    }

    public RegisteredOfficeAddressDetails getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public void setRegisteredOfficeAddressDetails(RegisteredOfficeAddressDetails registeredOfficeAddressDetails) {
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
    }

    public SecretaryDetails getSecretaryDetails() {
        return secretaryDetails;
    }

    public void setSecretaryDetails(SecretaryDetails secretaryDetails) {
        this.secretaryDetails = secretaryDetails;
    }

}
