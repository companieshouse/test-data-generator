package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "uvid")
public class Uvid {

    @Id
    private String id;

    /**
     * The @Field("uvid") annotation is kept to ensure
     * the MongoDB document field name remains "uvid".
     * The Java field name is changed to "value" to satisfy SonarQube.
     */
    @Field("uvid")
    private String value; // RENAMED

    @Field("type")
    private String type;

    @Field("identity_id")
    private String identityId;

    @Field("created")
    private Instant created;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIdentityId() {
        return identityId;
    }

    public void setIdentityId(String identityId) {
        this.identityId = identityId;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }
}