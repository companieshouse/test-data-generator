package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.List;
import java.util.Objects;

public class RoleSpec {

    @JsonProperty
    private String id;

    @JsonProperty
    private List<AdminPermissions> permissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AdminPermissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<AdminPermissions> permissions) {
        this.permissions = permissions;
    }

    public boolean isValid() {
        return id != null
                && !id.isEmpty()
                && permissions != null
                && !permissions.isEmpty()
                && permissions.stream().allMatch(Objects::nonNull);
    }
}