package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "roles")
public class Role {

    @Id
    @Field("id")
    private String id;

    @Field("permissions")
    private List<String> permissions;

    public String getId() {
        return id;
    }

    public List<String> getPermissions() {
        return permissions;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPermissions(List<String> permissions) {
        this.permissions = permissions;
    }
}
