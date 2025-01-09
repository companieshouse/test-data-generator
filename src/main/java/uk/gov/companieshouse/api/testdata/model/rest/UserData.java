package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserData {

  @JsonProperty("id")
  private final String id;

  @JsonProperty("email")
  private final String email;

  @JsonProperty("forename")
  private final String forename;

  @JsonProperty("surname")
  private final String surname;

  public UserData(String id, String email, String forename, String surname) {
    this.id = id;
    this.email = email;
    this.forename = forename;
    this.surname = surname;
  }

  public String getId() {
    return id;
  }

  public String getEmail() {
    return email;
  }

  public String getForename() {
    return forename;
  }

  public String getSurname() {
    return surname;
  }
}
