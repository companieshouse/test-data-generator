package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemOptionsSpec {
    @JsonProperty("certificate_type")
    private String certificateType;

    @JsonProperty("delivery_timescale")
    private String deliveryTimescale;

    @JsonProperty("include_email_copy")
    private boolean includeEmailCopy;

    @JsonProperty("company_type")
    private String companyType;

    @JsonProperty("company_status")
    private String companyStatus;

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

    public boolean getIncludeEmailCopy() {
        return includeEmailCopy;
    }

    public void setIncludeEmailCopy(boolean includeEmailCopy) {
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
}
