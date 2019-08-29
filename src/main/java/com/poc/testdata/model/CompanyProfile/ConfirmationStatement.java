
package com.poc.testdata.model.CompanyProfile;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ConfirmationStatement {

    @Field("last_made_up_to")
    private Date lastMadeUpTo;
    @Field("next_due")
    private Date nextDue;
    @Field("next_made_up_to")
    private Date nextMadeUpTo;
    @Field("overdue")
    private Boolean overdue;


    /**
     * No args constructor for use in serialization
     * 
     */
    public ConfirmationStatement() {
    }

    public Date getLastMadeUpTo() {
        return lastMadeUpTo;
    }

    public void setLastMadeUpTo(Date lastMadeUpTo) {
        this.lastMadeUpTo = lastMadeUpTo;
    }

    public Date getNextDue() {
        return nextDue;
    }

    public void setNextDue(Date nextDue) {
        this.nextDue = nextDue;
    }

    public Date getNextMadeUpTo() {
        return nextMadeUpTo;
    }

    public void setNextMadeUpTo(Date nextMadeUpTo) {
        this.nextMadeUpTo = nextMadeUpTo;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }
}
