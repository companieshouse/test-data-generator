package com.poc.testdata.model.FilingHistory;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.mongodb.core.mapping.Field;

import java.security.Key;
import java.util.HashMap;
import java.util.Map;

public class DescriptionValue {

    @Field("description_values")
    private Map<String, String> descriptionValues = null;

    public Map<String, String> getDescriptionValues() {
        return descriptionValues;
    }

    public void setDescriptionValues(Map<String, String> descriptionValues) {
        this.descriptionValues = descriptionValues;
    }
}
