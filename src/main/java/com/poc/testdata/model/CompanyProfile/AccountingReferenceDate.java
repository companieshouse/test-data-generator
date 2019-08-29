
package com.poc.testdata.model.CompanyProfile;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class AccountingReferenceDate {

    @Field("day")
    private Integer day;
    @Field("month")
    private Integer month;

    /**
     * No args constructor for use in serialization
     * 
     */
    public AccountingReferenceDate() {
    }

    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
