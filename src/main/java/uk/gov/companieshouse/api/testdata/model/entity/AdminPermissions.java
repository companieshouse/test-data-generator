package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@Document(collection = "admin_permissions")
public class AdminPermissions {

    @Id
    private String id;

    @JsonProperty("entra_group_id")
    private String entraGroupId;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty("permissions")
    private List<String> permissions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntraGroupId() {
        return entraGroupId;
    }

    public void setEntraGroupId(String entraGroupId) {
        this.entraGroupId = entraGroupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}