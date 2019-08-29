
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

public class NextAccounts {


    @Field("due_on")
    private Date dueOn;
    @Field("overdue")
    private Boolean overdue;
    @Field("period_end_on")
    private Date periodEndOn;
    @Field("period_start_on")
    private Date periodStartOn;


    /**
     * No args constructor for use in serialization
     * 
     */
    public NextAccounts() {
    }

    public Date getDueOn() {
        return dueOn;
    }

    public void setDueOn(Date dueOn) {
        this.dueOn = dueOn;
    }

    public Boolean getOverdue() {
        return overdue;
    }

    public void setOverdue(Boolean overdue) {
        this.overdue = overdue;
    }

    public Date getPeriodEndOn() {
        return periodEndOn;
    }

    public void setPeriodEndOn(Date periodEndOn) {
        this.periodEndOn = periodEndOn;
    }

    public Date getPeriodStartOn() {
        return periodStartOn;
    }

    public void setPeriodStartOn(Date periodStartOn) {
        this.periodStartOn = periodStartOn;
    }
}
