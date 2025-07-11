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
    private final String userId;

     @JsonProperty("created_by.email")
    private final String email;

       @JsonProperty("description")
    private final String description;

       @JsonProperty("reference")
    private final String reference;

    @JsonProperty("resume_journey_uri")
    private final String resume_uri;

    @JsonProperty("status")
    private final String status;

    public TransactionsData(String id, String email,String forename, String surname,String userId, String description , String reference, String resume_uri, String status) {
        this.id = id;
        this.email = email;
        this.forename = forename;
        this.surname = surname;
        this.userId = userId;
        this.description = description;
        this.reference = reference;
        this.resume_uri=resume_uri;
        this.status = status;
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
        return userId;
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

    public String getResume_uri(){return resume_uri;}

    public String getStatus(){return status;}
   
}
