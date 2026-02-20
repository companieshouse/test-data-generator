package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ItemOptionsRequest {
    @JsonProperty("certificate_type")
    private String certificateType;

    @JsonProperty("collection_location")
    private String collectionLocation;

    @JsonProperty("contact_number")
    private String contactNumber;

    @JsonProperty("delivery_timescale")
    private String deliveryTimescale;

    @JsonProperty("delivery_method")
    private String deliveryMethod;

    @JsonProperty("include_email_copy")
    private Boolean includeEmailCopy;

    @JsonProperty("include_company_objects_information")
    private Boolean includeCompanyObjectsInformation;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("filing_history_documents")
    private List<FilingHistoryDocumentsRequest> filingHistoryDocuments;

    @JsonProperty("forename")
    private String foreName;

    @JsonProperty("surname")
    private String surName;

    @JsonProperty("filing_history_date")
    private String filingHistoryDate;

    @JsonProperty("filing_history_description")
    private String filingHistoryDescription;

    @JsonProperty("filing_history_description_values")
    private FilingHistoryDescriptionValuesRequest filingHistoryDescriptionValues;

    @JsonProperty("filing_history_id")
    private String filingHistoryId;

    @JsonProperty("filing_history_type")
    private String filingHistoryType;

    @JsonProperty("filing_history_category")
    private String filingHistoryCategory;

    @JsonProperty("filing_history_barcode")
    private String filingHistoryBarcode;

    @JsonProperty("director_details")
    private DirectorDetailsRequest directorDetails;

    @JsonProperty("include_good_standing_information")
    private Boolean includeGoodStandingInformation;

    @JsonProperty("registered_office_address_details")
    private RegisteredOfficeAddressDetailsSpec registeredOfficeAddressDetails;

    @JsonProperty("secretary_details")
    private SecretaryDetailsSpec secretaryDetails;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getDeliveryTimescale() {
        return  deliveryTimescale;
    }

    public void setDeliveryTimescale(String deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
    }

    public String getDeliveryMethod() {
        return  deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public Boolean getIncludeEmailCopy() {
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

    public List<FilingHistoryDocumentsRequest> getFilingHistoryDocuments() {
        return filingHistoryDocuments;
    }

    public void setFilingHistoryDocumentsSpec(List<FilingHistoryDocumentsRequest> filingHistoryDocuments) {
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

    public FilingHistoryDescriptionValuesRequest getFilingHistoryDescriptionValues() {
        return filingHistoryDescriptionValues;
    }

    public void setFilingHistoryDescriptionValues(FilingHistoryDescriptionValuesRequest filingHistoryDescriptionValues) {
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

    public DirectorDetailsRequest getDirectorDetails() {
        return directorDetails;
    }

    public void setDirectorDetails(DirectorDetailsRequest directorDetails) {
        this.directorDetails = directorDetails;
    }

    public Boolean getIncludeCompanyObjectsInformation() {
        return  includeCompanyObjectsInformation;
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

    public RegisteredOfficeAddressDetailsSpec getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public void setRegisteredOfficeAddressDetails(RegisteredOfficeAddressDetailsSpec registeredOfficeAddressDetails) {
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
    }

    public SecretaryDetailsSpec getSecretaryDetails() {
        return secretaryDetails;
    }

    public void setSecretaryDetails(SecretaryDetailsSpec secretaryDetails) {
        this.secretaryDetails = secretaryDetails;
    }
}
