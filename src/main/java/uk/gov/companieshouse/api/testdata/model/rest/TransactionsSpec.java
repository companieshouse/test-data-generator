package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class TransactionsSpec {

    @JsonProperty
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
            message = "email is not a valid email address")
    private String email;

    @JsonProperty
    @NotEmpty(message = "description is required")
    private String description;

       @JsonProperty
    @NotEmpty(message = "id is required")
    private String id;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String desription){
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
