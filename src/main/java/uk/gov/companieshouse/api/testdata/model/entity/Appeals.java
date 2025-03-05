package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "appeals")
public class Appeals {

    @Id
    @Field("_id")
    private String id;

    @Field("penaltyIdentifier.penaltyReference")
    private String penaltyReference;

    @Field("penaltyIdentifier.companyNumber")
    private String companyNumber;

    public String getPenaltyReference() {
        return penaltyReference;
    }

    public void setPenaltyReference(String penaltyReference) {
        this.penaltyReference = penaltyReference;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
