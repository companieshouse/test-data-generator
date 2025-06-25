package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class DisqualificationsData {

    @JsonProperty("id")
    private final String id;

    @JsonProperty("date_of_birth")
    private final Date dateOfBirth;

    @JsonProperty("disqualifications_uri")
    private final String disqualificationsUri;

    public DisqualificationsData(String id, Date dateOfBirth, String disqualificationsUri) {
        this.id = id;
        this.dateOfBirth = dateOfBirth;
        this.disqualificationsUri = disqualificationsUri;
    }

    public String getId() {
        return id;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getDisqualificationsUri() {
        return disqualificationsUri;
    }
}
