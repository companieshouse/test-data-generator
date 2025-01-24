package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.Date;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "acsp_members")
public class AcspMembers {

    @Id
    @Field("_id")
    private String acspMemberId;

    @Field("acsp_number")
    private String acspNumber;

    @Field("user_id")
    private String userId;

    @Field("user_role")
    private String userRole;

    @Field("created_at")
    private Date createdAt;

    @Field("added_at")
    private Date addedAt;

    @Field("status")
    private String status;

    @Field("etag")
    private String etag;

    @Field("version")
    private long version;

    public String getAcspMemberId() {
        return acspMemberId;
    }

    public String getAcspNumber() {
        return acspNumber;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getEtag() {
        return etag;
    }

    public long getVersion() {
        return version;
    }

    public void setAcspMemberId(String acspMemberId) {
        this.acspMemberId = acspMemberId;
    }

    public void setAcspNumber(String acspNumber) {
        this.acspNumber = acspNumber;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setAddedAt(Date addedAt) {
        this.addedAt = addedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}