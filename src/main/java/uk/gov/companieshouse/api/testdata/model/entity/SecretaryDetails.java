package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class SecretaryDetails {

    @Field("include_address")
    private Boolean includeAddress;

    @Field("include_appointment_date")
    private Boolean includeAppointmentDate;

    @Field("include_basic_information")
    private Boolean includeBasicInformation;

    @Field("include_country_of_residence")
    private Boolean includeCountryOfResidence;

    @Field("include_dob_type")
    private String includeDobType;

    @Field("include_nationality")
    private Boolean includeNationality;

    @Field("include_occupation")
    private Boolean includeOccupation;

    public Boolean getIncludeAddress() {
        return includeAddress;
    }

    public void setIncludeAddress(Boolean includeAddress) {
        this.includeAddress = includeAddress;
    }

    public Boolean getIncludeAppointmentDate() {
        return includeAppointmentDate;
    }

    public void setIncludeAppointmentDate(Boolean includeAppointmentDate) {
        this.includeAppointmentDate = includeAppointmentDate;
    }

    public Boolean getIncludeBasicInformation() {
        return includeBasicInformation;
    }

    public void setIncludeBasicInformation(Boolean includeBasicInformation) {
        this.includeBasicInformation = includeBasicInformation;
    }

    public Boolean getIncludeCountryOfResidence() {
        return includeCountryOfResidence;
    }

    public void setIncludeCountryOfResidence(Boolean includeCountryOfResidence) {
        this.includeCountryOfResidence = includeCountryOfResidence;
    }

    public String getIncludeDobType() {
        return includeDobType;
    }

    public void setIncludeDobType(String includeDobType) {
        this.includeDobType = includeDobType;
    }

    public Boolean getIncludeNationality() {
        return includeNationality;
    }

    public void setIncludeNationality(Boolean includeNationality) {
        this.includeNationality = includeNationality;
    }

    public Boolean getIncludeOccupation() {
        return includeOccupation;
    }

    public void setIncludeOccupation(Boolean includeOccupation) {
        this.includeOccupation = includeOccupation;
    }
}
