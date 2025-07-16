package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class PreviousState {
    @Field("status")
    private String status;

    @Field("changed_at")
    private Instant changedAt;

    @Field("changed_by")
    private String changedBy;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }
}