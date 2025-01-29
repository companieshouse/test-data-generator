package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "company_auth_allow_list")
public class CompanyAuthAllowList {

    @Id
    @Field("_id")
    private String id;

    @Field("emailAddress")
    private String emailAddress;

    public String getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }
}
