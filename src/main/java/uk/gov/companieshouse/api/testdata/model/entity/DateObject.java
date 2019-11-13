package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

public class DateObject {

    @Field("at")
    private LocalDateTime at;

    public DateObject(LocalDateTime at) {
        this.at = at;
    }

    public LocalDateTime getAt() {
        return at;
    }

    public void setAt(LocalDateTime at) {
        this.at = at;
    }
}
