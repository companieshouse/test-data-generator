package uk.gov.companieshouse.api.testdata.model.entity;

import java.time.Instant;
import org.springframework.data.mongodb.core.mapping.Field;

public class Invitation {
    @Field("invited_by")
    private String invitedBy;

    @Field("invited_at")
    private Instant invitedAt;

    public String getInvitedBy() {
        return invitedBy;
    }

    public void setInvitedBy(String invitedBy) {
        this.invitedBy = invitedBy;
    }

    public Instant getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
    }
}