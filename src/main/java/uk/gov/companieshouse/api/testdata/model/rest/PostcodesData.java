package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;


public class PostcodesData {

    @JsonProperty("building_number")
    private final Number buildingNumber;
    @JsonProperty("first_line")
    private final String firstLine;
    @JsonProperty("dependent_locality")
    private final String dependentLocality;
    @JsonProperty("post_town")
    private final String postTown;
    @JsonProperty("postcode")
    private final String postcode;

    public PostcodesData(Number buildingNumber, String firstLine,
                         String dependentLocality, String postTown, String postcode) {
        this.buildingNumber = buildingNumber;
        this.firstLine = firstLine;
        this.dependentLocality = dependentLocality;
        this.postTown = postTown;
        this.postcode = postcode;
    }

    public String getFirstLine() {
        return firstLine;
    }

    public String getDependentLocality() {
        return dependentLocality;
    }

    public String getPostTown() {
        return postTown;
    }

    public String getPostcode() {
        return postcode;
    }

    public Number getBuildingNumber() {
        return buildingNumber;
    }
}
