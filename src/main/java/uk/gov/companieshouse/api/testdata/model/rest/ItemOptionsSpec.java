package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ItemOptionsSpec {
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
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean includeEmailCopy;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("company_status")
    private String companyStatus;

    @JsonProperty("filing_history_documents")
    private List<FilingHistoryDocumentsSpec> filingHistoryDocuments;

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

    public List<FilingHistoryDocumentsSpec> getFilingHistoryDocuments() {
        return filingHistoryDocuments;
    }

    public void setFilingHistoryDocumentsSpec(List<FilingHistoryDocumentsSpec> filingHistoryDocuments) {
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
}
