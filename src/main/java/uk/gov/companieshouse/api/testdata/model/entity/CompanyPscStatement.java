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
    private Links dataLinks;
    @Field("data.notified_on")
    private Instant dataNotifiedOn;
    @Field("data.etag")
    private String dataEtag;
    @Field("data.dataKind")
    private String dataKind;
    @Field("data.statement")
    private String dataStatement;
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

    public Links getDataLinks() {
        return dataLinks;
    }

    public void setDataLinks(Links dataLinks) {
        this.dataLinks = dataLinks;
    }

    public Instant getDataNotifiedOn() {
        return dataNotifiedOn;
    }

    public void setDataNotifiedOn(Instant dataNotifiedOn) {
        this.dataNotifiedOn = dataNotifiedOn;
    }

    public String getDataEtag() {
        return dataEtag;
    }

    public void setDataEtag(String dataEtag) {
        this.dataEtag = dataEtag;
    }

    public String getDataKind() {
        return dataKind;
    }

    public void setDataKind(String dataKind) {
        this.dataKind = dataKind;
    }

    public String getDataStatement() {
        return dataStatement;
    }

    public void setDataStatement(String dataStatement) {
        this.dataStatement = dataStatement;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
