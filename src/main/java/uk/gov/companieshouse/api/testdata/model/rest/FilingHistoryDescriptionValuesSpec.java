package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FilingHistoryDescriptionValuesSpec {
    @JsonProperty("date")
    private String date;

    @JsonProperty("capital")
    private List<CapitalSpec> capital;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<CapitalSpec> getCapital() {
        return capital;
    }

    public void setCapital(List<CapitalSpec> capital) {
        this.capital = capital;
    }
}
