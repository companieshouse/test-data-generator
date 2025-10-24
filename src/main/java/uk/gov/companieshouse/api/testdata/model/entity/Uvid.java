package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.bson.types.ObjectId;

import java.time.Instant;

@Document(collection = "identityUvid")
public class Uvid {

    @Id
    private ObjectId id;

    @Field("identity_uvid")
    private String identityUvid;

    @Field("type")
    private String type;

    @Field("identity_id")
    private String identityId;

    @Field("created")
    private Instant created;

    public String getId() {
        return id != null ? id.toHexString() : null;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getIdentityUvid() {
        return identityUvid;
    }

    public void setIdentityUvid(String identityUvid) {
        this.identityUvid = identityUvid;
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