package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.testdata.model.entity.Address;

public enum Jurisdiction {

    @JsonProperty("england/wales")
    ENGLAND_WALES(new Address(
            "Companies House",
            "Crownway",
            "United Kingdom",
            "Cardiff",
            "CF14 3UZ"
    ), "Wales"),
    @JsonProperty("scotland")
    SCOTLAND(new Address(
            "4th Floor Edinburgh Quay 2",
            "139 Fountain Bridge",
            "United Kingdom",
            "Edinburgh",
            "EH3 9FF"
    ), "Scotland"),
    @JsonProperty("northern-ireland")
    NI(new Address(), "NI");

    private Address address;
    private String countryOfResidence;

    Jurisdiction(Address address, String countryOfResidence) {
        this.address = address;
        this.countryOfResidence = countryOfResidence;
    }

    public Address getAddress() {
        return address;
    }

    public String getCountryOfResidence() {
        return countryOfResidence;
    }
}
