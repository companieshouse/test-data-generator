package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Document(collection = "company_psc_statements")
public class CompanyPscStatement {

    @Id
    @Field("id")
    private String id;
    @Field("updated.at")
    private Instant updatedAt;
    @Field("company_number")
    private String companyNumber;
    @Field("psc_statement_id")
    private String pscStatementId;
    @Field("data.links")
    private Links links;
    @Field("data.notified_on")
    private Instant notifiedOn;
    @Field("data.etag")
    private String etag;
    @Field("data.kind")
    private String kind;
    @Field("data.statement")
    private String statement;
    @Field("created.at")
    private Instant createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getPscStatementId() {
        return pscStatementId;
    }

    public void setPscStatementId(String pscStatementId) {
        this.pscStatementId = pscStatementId;
    }

    public Links getLinks() {
        return links;
    }

    public void setLinks(Links links) {
        this.links = links;
    }

    public Instant getNotifiedOn() {
        return notifiedOn;
    }

    public void setNotifiedOn(Instant notifiedOn) {
        this.notifiedOn = notifiedOn;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
