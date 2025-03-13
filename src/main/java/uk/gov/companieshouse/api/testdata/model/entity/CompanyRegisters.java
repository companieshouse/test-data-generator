package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "company_registers")
public class CompanyRegisters {

    @Id
    @Field
    private String id;

    @Field("created.at")
    private LocalDate createdAt;

    @Field("data.company_number")
    private String companyNumber;

    @Field("data.kind")
    private String kind;

    @Field("data.links.self")
    private String selfLink;

    @Field("data.etag")
    private String etag;

    @Field("data.registers")
    private Map<String, Register> registers;

    @Field("delta_at")
    private LocalDate deltaAt;

    @Field("updated.at")
    private LocalDate updatedAt;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public void setSelfLink(String selfLink) {
        this.selfLink = selfLink;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public Map<String, Register> getRegisters() {
        return registers;
    }

    public void setRegisters(Map<String, Register> registers) {
        this.registers = registers;
    }

    public LocalDate getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(LocalDate deltaAt) {
        this.deltaAt = deltaAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}