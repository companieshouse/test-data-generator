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

    @Field("include_email_copy")
    private Boolean includeEmailCopy;

    @Field("company_type")
    private String companyType;

    @Field("company_status")
    private String companyStatus;

    @Field("filing_history_documents")
    private List<FilingHistoryDocument> filingHistoryDocuments;

    @Field("forename")
    private String foreName;

    @Field("surname")
    private String surName;

    public String getCertificateType() {
        return certificateType;
    }

    public void setCertificateType(String certificateType) {
        this.certificateType = certificateType;
    }

    public String getDeliveryMethod() { return deliveryMethod; }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getDeliveryTimescale() {
        return  deliveryTimescale;
    }

    public void setDeliveryTimescale(String deliveryTimescale) {
        this.deliveryTimescale = deliveryTimescale;
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

    public String getCollectionLocation() { return collectionLocation; }

    public void setCollectionLocation(String collectionLocation) {
        this.collectionLocation = collectionLocation;
    }

    public String getContactNumber() { return contactNumber; }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getForeName() { return foreName; }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    public String getSurName() { return surName; }

    public void setSurName(String surName) {
        this.surName = surName;
    }
}
