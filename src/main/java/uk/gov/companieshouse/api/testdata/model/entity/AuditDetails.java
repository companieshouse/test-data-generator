package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class AuditDetails {

    @Field("at")
    private Instant at;

    @Field("by")
    private String by;

    @Field("type")
    private String type;

    public Instant getAt() { return at; }
    public void setAt(Instant at) { this.at = at; }

    public String getBy() { return by; }
    public void setBy(String by) { this.by = by; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}
