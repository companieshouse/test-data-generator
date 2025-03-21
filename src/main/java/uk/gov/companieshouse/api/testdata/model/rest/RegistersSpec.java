package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

public class RegistersSpec {

    @JsonProperty("register_type")
    @Pattern(regexp = "directors|secretaries|persons-with-significant-control|usual-residential-address|members|llp-members|llp-usual-residential-address", message = "Invalid register type")
    private String registerType;

    @Pattern(regexp = "registered-office|single-alternative-inspection-location|public-register|unspecified-location", message = "Invalid register locations")
    @JsonProperty("register_moved_to")
    private String registerMovedTo;

    public String getRegisterType() {
        return registerType;
    }

    public void setRegisterType(String registerType) {
        this.registerType = registerType;
    }

    public String getRegisterMovedTo() {
        return registerMovedTo;
    }

    public void setRegisterMovedTo(String registerMovedTo) {
        this.registerMovedTo = registerMovedTo;
    }
}
