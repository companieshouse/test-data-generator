package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectorDetailsRequest {
    @JsonProperty("include_address")
    private Boolean includeAddress;

    @JsonProperty("include_appointment_date")
    private Boolean includeAppointmentDate;

    @JsonProperty("include_basic_information")
    private Boolean includeBasicInformation;

    @JsonProperty("include_country_of_residence")
    private Boolean includeCountryOfResidence;

    @JsonProperty("include_dob_type")
    private String includeDobType;

    @JsonProperty("include_nationality")
    private Boolean includeNationality;

    @JsonProperty("include_occupation")
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
