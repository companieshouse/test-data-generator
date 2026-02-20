package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class BasketRequest {

    @JsonProperty("forename")
    @NotBlank(message = "Forename is required")
    private String forename;

    @JsonProperty("surname")
    @NotBlank(message = "Surname is required")
    private String surname;

    @JsonProperty("enrolled")
    @NotNull(message = "Enrolled flag must be set")
    private Boolean enrolled;

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Boolean getEnrolled() {
        return enrolled;
    }

    public void setEnrolled(Boolean enrolled) {
        this.enrolled = enrolled;
    }
}
