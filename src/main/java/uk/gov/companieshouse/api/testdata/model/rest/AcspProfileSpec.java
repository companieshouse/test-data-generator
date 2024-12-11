package uk.gov.companieshouse.api.testdata.model.rest;

import jakarta.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Requirements an ACSP profile must meet
 *
 */
public class AcspProfileSpec {

    @JsonProperty
    @NotNull(message = "ACSP number is required")
    private String acspNumber;

    @JsonProperty
    @NotNull(message = "Name is required")
    private String name;

    @JsonIgnore
    private String etag;

    public AcspProfileSpec() {
        // Default constructor
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }
}
