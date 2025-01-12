package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "acsp_profile")
public class AcspProfile {

    @Id
    @Field("_id")
    private String id;

    @Field("version")
    private Long version;

    @Field("data.acsp_number")
    private String acspNumber;

    @Field("data.name")
    private String name;

    @Field("data.status")
    private String status;

    @Field("data.type")
    private String type;

    @Field("data.links.self")
    private String linksSelf;

    public String getId() {
        return id;
    }

    public Long getVersion(long l) {
        return version;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setLinksSelf(String linksSelf) {
        this.linksSelf = linksSelf;
    }
}
