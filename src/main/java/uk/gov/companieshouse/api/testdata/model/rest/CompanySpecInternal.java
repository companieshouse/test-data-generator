package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

import java.time.LocalDate;
import java.util.List;

public class CompanySpecInternal {

    @JsonProperty("data")
    private CompanyProfileApi data;

    @JsonProperty("has_mortgage")
    private Boolean hasMortgage;

    @JsonProperty("delta_at")
    private String deltaAt;

    public CompanyProfileApi getData() {
        return data;
    }

    public void setData(CompanyProfileApi data) {
        this.data = data;
    }

    public Boolean getHasMortgage() {
        return hasMortgage;
    }

    public void setHasMortgage(Boolean hasMortgage) {
        this.hasMortgage = hasMortgage;
    }

    public String getDeltaAt() {
        return deltaAt;
    }

    public void setDeltaAt(String deltaAt) {
        this.deltaAt = deltaAt;
    }


}