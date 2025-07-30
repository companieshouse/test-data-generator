package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RegisteredOfficeAddressDetailsSpec {
    @JsonProperty("include_address_records_type")
    private String includeAddressRecordsType;

    @JsonProperty("include_dates")
    private Boolean includeDates;

    public String getIncludeAddressRecordsType() {
        return includeAddressRecordsType;
    }

    public void setIncludeAddressRecordsType(String includeAddressRecordsType) {
        this.includeAddressRecordsType = includeAddressRecordsType;
    }

    public Boolean getIncludeDates() {
        return includeDates;
    }

    public void setIncludeDates(Boolean includeDates) {
        this.includeDates = includeDates;
    }
}
