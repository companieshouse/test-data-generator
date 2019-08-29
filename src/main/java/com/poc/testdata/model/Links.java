
package com.poc.testdata.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashMap;
import java.util.Map;

public class Links {

    @Field("charges")
    private String charges;
    @Field("filing_history")
    private String filingHistory;
    @Field("insolvency")
    private String insolvency;
    @Field("officers")
    private String officers;
    @Field("persons_with_significant_control")
    private String personsWithSignificantControl;
    @Field("persons_with_significant_control_statements")
    private String personsWithSignificantControlStatments;
    @Field("registers")
    private String registers;
    @Field("self")
    private String self;


    /**
     * No args constructor for use in serialization
     * 
     */
    public Links() {
    }

    public String getCharges() {
        return charges;
    }

    public void setCharges(String charges) {
        this.charges = charges;
    }

    public String getFilingHistory() {
        return filingHistory;
    }

    public void setFilingHistory(String filingHistory) {
        this.filingHistory = filingHistory;
    }

    public String getInsolvency() {
        return insolvency;
    }

    public void setInsolvency(String insolvency) {
        this.insolvency = insolvency;
    }

    public String getOfficers() {
        return officers;
    }

    public void setOfficers(String officers) {
        this.officers = officers;
    }

    public String getPersonsWithSignificantControl() {
        return personsWithSignificantControl;
    }

    public void setPersonsWithSignificantControl(String personsWithSignificantControl) {
        this.personsWithSignificantControl = personsWithSignificantControl;
    }

    public String getPersonsWithSignificantControlStatments() {
        return personsWithSignificantControlStatments;
    }

    public void setPersonsWithSignificantControlStatments(String personsWithSignificantControlStatments) {
        this.personsWithSignificantControlStatments = personsWithSignificantControlStatments;
    }

    public String getRegisters() {
        return registers;
    }

    public void setRegisters(String registers) {
        this.registers = registers;
    }

    public String getSelf() {
        return self;
    }

    public void setSelf(String self) {
        this.self = self;
    }
}
