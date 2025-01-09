package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class RoleSpec {

  @JsonProperty private String id;

  @JsonProperty private List<String> permissions;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }

  public boolean isValid() {
    return id != null
        && !id.isEmpty()
        && permissions != null
        && !permissions.isEmpty()
        && permissions.stream().allMatch(permission -> permission != null && !permission.isEmpty());
  }
}
