package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "company_auth_codes")
public class CompanyAuthCode {

    @Id
    @Field("id")
    private String id;
    @Transient
    private String authCode;
    @Field("auth_code")
    private String encryptedAuthCode;
    @Field("is_active")
    private Boolean isActive;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getEncryptedAuthCode() {
        return encryptedAuthCode;
    }

    public void setEncryptedAuthCode(String encryptedAuthCode) {
        this.encryptedAuthCode = encryptedAuthCode;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }
}
