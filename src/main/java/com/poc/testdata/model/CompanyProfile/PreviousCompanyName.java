
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

public class PreviousCompanyName {

    @Field("ceased_on")
    private Date ceasedOn;
    @Field("effective_from")
    private Date effectiveFrom;
    @Field("name")
    private String name;


    /**
     * No args constructor for use in serialization
     * 
     */
    public PreviousCompanyName() {
    }

    public Date getCeasedOn() {
        return ceasedOn;
    }

    public void setCeasedOn(Date ceasedOn) {
        this.ceasedOn = ceasedOn;
    }

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
