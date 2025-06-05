package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class DescriptionValuesSpec {

    @JsonProperty("capital")
    private List<CapitalSpec> capital;

    public List<CapitalSpec> getCapital() {
        return capital;
    }

    public void setCapital(List<CapitalSpec> capital) {
        this.capital = capital;
    }
}
