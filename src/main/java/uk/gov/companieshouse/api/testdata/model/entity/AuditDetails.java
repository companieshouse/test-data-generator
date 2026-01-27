package uk.gov.companieshouse.api.testdata.model.entity;
import java.time.Instant;

public class AuditDetails {
    private Instant at;
    private String by;
    private String type;

    public Instant getAt() { return at; }

    public void setAt(Instant at) { this.at = at; }

    public String getBy() { return by; }

    public void setBy(String by) { this.by = by; }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }
}
