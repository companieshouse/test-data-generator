package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "transactions")
public class Transactions {

    @Id
    @Field("_id")
    private String id;

    @Field("created_by.forname")
    private String forename;

    @Field("created_by.surname")
    private String surname;

    @Field("created_by.id")
    private String user_id;

    @Field("created_by.email")
    private String email;

    @Field("description")
    private String description;

    @Field("reference")
    private String reference;

    @Field("resume_journey_uri")
    private String resume_uri;

    @Field("status")
    private String status;




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

    public String getResume_uri(){ return resume_uri;}

    public String getStatus(){ return status;}

   
    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void setForename(String forename) {
        this.forename = forename;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

     public void setUserId(String user_id) {
        this.user_id = user_id;
    }

     public void setDescription(String description) {
        this.description = description;
    }

 public void setReference(String reference) {
        this.reference = reference;
    }

    public void setResume_uri(String resume_uri){this.resume_uri=resume_uri;}

    public void setStatus(String status){this.status=status;}
    
}
