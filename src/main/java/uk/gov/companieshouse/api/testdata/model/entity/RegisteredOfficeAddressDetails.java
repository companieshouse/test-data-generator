package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class RegisteredOfficeAddressDetails {

    @Field("include_address_records_type")
    private String includeAddressRecordsType;

    @Field("include_dates")
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
