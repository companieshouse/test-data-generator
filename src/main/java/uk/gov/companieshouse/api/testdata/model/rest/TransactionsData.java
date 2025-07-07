package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionsData {

    @JsonProperty("_id")
    private final String id;

    @JsonProperty("created_by.forname")
    private final String forename;

    @JsonProperty("created_by.surname")
    private final String surname;

 @JsonProperty("created_by.id")
    private final String user_id;

     @JsonProperty("created_by.email")
    private final String email;

       @JsonProperty("description")
    private final String description;

       @JsonProperty("reference")
    private final String reference;

    public TransactionsData(String id, String email, String forename, String surname,String user_id, String description , String reference) {
        this.id = id;
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.user_id = user_id;
        this.description = description;
        this.reference = reference;
    }

   public String getId() {
        return id;
    }


    public String getForename() {
        return forename;
    }

    public String getSurname() {
        return surname;
    }

     public String getUserId() {
        return user_id;
    }

 public String getEmail() {
        return email;
    }

 public String getDescription() {
        return description;
    }

     public String getReference() {
        return reference;
    }

   
}
