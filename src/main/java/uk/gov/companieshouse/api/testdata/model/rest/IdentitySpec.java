package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Field;

public class IdentitySpec {
    @JsonProperty("user_id")
    @NotNull(message = "user_id is required")
    private String userId;

    @JsonProperty
    @NotNull(message = "email is required")
    private String email;

    @JsonProperty("verification_source")
    @NotNull(message = "verification_source is required")
    private String verificationSource;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVerificationSource() {
        return verificationSource;
    }

    public void setVerificationSource(String verificationSource) {
        this.verificationSource = verificationSource;
    }
}
