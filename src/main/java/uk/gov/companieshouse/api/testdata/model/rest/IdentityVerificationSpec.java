package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IdentityVerificationSpec {
    @NotNull
    @JsonProperty("verification_source")
    @Pattern(regexp = "(ONE_LOGIN|ACSP)",
            message = "verification_source must be either 'ONE_LOGIN' or 'ACSP'")
    private String verificationSource;

    public  String getVerificationSource() {
        return verificationSource;
    }

    public void setVerificationSource(String verificationSource) {
        this.verificationSource = verificationSource;
    }
}
