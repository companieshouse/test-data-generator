
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

public class LastAccounts {


    @Field("made_up_to")
    private Date madeUpTo;
    @Field("period_end_on")
    private Date periodEndOn;
    @Field("period_start_on")
    private Date periodStartOn;
    @Field("type")
    private String type;


    /**
     * No args constructor for use in serialization
     * 
     */
    public LastAccounts() {
    }

    public Date getMadeUpTo() {
        return madeUpTo;
    }

    public void setMadeUpTo(Date madeUpTo) {
        this.madeUpTo = madeUpTo;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
