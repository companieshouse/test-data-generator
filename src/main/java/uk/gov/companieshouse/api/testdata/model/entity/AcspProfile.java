package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;
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

    @Field("data.etag")
    private String etag;

    @Field("data.links.self")
    private String linksSelf;

    @Field("data.aml_details")
    private List<AmlDetails> amlDetails;

    @Field("sensitive_data.email")
    private String email;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getLinksSelf() {
        return linksSelf;
    }

    public void setLinksSelf(String linksSelf) {
        this.linksSelf = linksSelf;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public long getVersion() {
        return version;
    }

    public List<AmlDetails> getAmlDetails() {
        return amlDetails;
    }

    public void setAmlDetails(List<AmlDetails> amlDetails) {
        this.amlDetails = amlDetails;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
