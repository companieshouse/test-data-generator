package uk.gov.companieshouse.api.testdata.model.entity;

import java.beans.ConstructorProperties;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

public class ItemOptions {
    @Field("certificate_type")
    private String certificateType;

    @Field("delivery_timescale")
    private String deliveryTimescale;

    @Field("director_details")
    private Object directorDetails;

    @Field("include_email_copy")
    private boolean includeEmailCopy;

    @Field("registered_office_address_details")
    private Object registeredOfficeAddressDetails;

    @Field("secretary_details")
    private Object secretaryDetails;

    @Field("company_type")
    private String companyType;

    @Field("company_status")
    private String companyStatus;

    @Field("administrators_details.include_basic_information")
    private boolean includeBasicInformation;

    public ItemOptions() {
    }

    @ConstructorProperties({"certificate_type", "delivery_timescale", "director_details", "include_email_copy",
            "registered_office_address_details", "secretary_details", "company_type",
            "company_status", "administrators_details.include_basic_information"})
    public ItemOptions(String certificateType, String deliveryTimescale, Object directorDetails, boolean includeEmailCopy,
                       Object registeredOfficeAddressDetails, Object secretaryDetails, String companyType,
                       String companyStatus, boolean includeBasicInformation) {
        this.certificateType = certificateType;
        this.deliveryTimescale = deliveryTimescale;
        this.directorDetails = directorDetails;
        this.includeEmailCopy = includeEmailCopy;
        this.registeredOfficeAddressDetails = registeredOfficeAddressDetails;
        this.secretaryDetails = secretaryDetails;
        this.companyType = companyType;
        this.companyStatus = companyStatus;
        this.includeBasicInformation = includeBasicInformation;
    }

    public String getCertificateType() {
        return certificateType;
    }

    public String getDeliveryTimescale() {
        return deliveryTimescale;
    }

    public Object getDirectorDetails() {
        return directorDetails;
    }

    public boolean isIncludeEmailCopy() {
        return includeEmailCopy;
    }

    public Object getRegisteredOfficeAddressDetails() {
        return registeredOfficeAddressDetails;
    }

    public Object getSecretaryDetails() {
        return secretaryDetails;
    }

    public String getCompanyType() {
        return companyType;
    }

    public String getCompanyStatus() {
        return companyStatus;
    }

    public boolean isIncludeBasicInformation() {
        return includeBasicInformation;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ItemOptions)) {
            return false;
        }
        var itemOptions = (ItemOptions) obj;
        return includeEmailCopy == itemOptions.includeEmailCopy
                && includeBasicInformation == itemOptions.includeBasicInformation
                && Objects.equals(certificateType, itemOptions.certificateType)
                && Objects.equals(deliveryTimescale, itemOptions.deliveryTimescale)
                && Objects.equals(directorDetails, itemOptions.directorDetails)
                && Objects.equals(registeredOfficeAddressDetails, itemOptions.registeredOfficeAddressDetails)
                && Objects.equals(secretaryDetails, itemOptions.secretaryDetails)
                && Objects.equals(companyType, itemOptions.companyType)
                && Objects.equals(companyStatus, itemOptions.companyStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateType, deliveryTimescale, directorDetails, includeEmailCopy, registeredOfficeAddressDetails,
                secretaryDetails, companyType, companyStatus, includeBasicInformation);
    }
}
