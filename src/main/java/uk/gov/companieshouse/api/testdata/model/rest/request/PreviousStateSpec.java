package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;

public class PreviousStateSpec {
    @JsonProperty("status")
    @Pattern(regexp = "confirmed|awaiting-approval|removed|migrated|unauthorised",
            message = "Invalid association status")
    private String status;

    @JsonProperty("changed_by")
    private String changedBy;

    @JsonProperty("changed_at")
    private Instant changedAt;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(String changedBy) {
        this.changedBy = changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(Instant changedAt) {
        this.changedAt = changedAt;
    }
}