package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class IdentityVerificationRequest {
    @NotNull
    @JsonProperty("verification_source")
    @Pattern(regexp = "(ONE_LOGIN|ACSP)",
            message = "incorrect verification source")
    private String verificationSource;

    public  String getVerificationSource() {
        return verificationSource;
    }

    public void setVerificationSource(String verificationSource) {
        this.verificationSource = verificationSource;
    }
}
