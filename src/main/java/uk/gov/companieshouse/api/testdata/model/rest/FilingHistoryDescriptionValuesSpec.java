package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class FilingHistoryDescriptionValuesSpec {
    @JsonProperty("date")
    private String date;

    @JsonProperty("charge_number")
    private String chargeNumber;

    @JsonProperty("made_up_date")
    private String madeUpDate;

    @JsonProperty("officer_name")
    private String officerName;

    @JsonProperty("capital")
    private List<CapitalSpec> capital;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getChargeNumber() { return chargeNumber; }

    public void setChargeNumber(String chargeNumber) {
        this.chargeNumber = chargeNumber;
    }

    public String getMadeUpDate() { return madeUpDate; }

    public void setMadeUpDate(String madeUpDate) {
        this.madeUpDate = madeUpDate;
    }

    public String getOfficerName() { return officerName; }

    public void setOfficerName(String officerName) {
         this.officerName = officerName;
    }

    public List<CapitalSpec> getCapital() {
        return capital;
    }

    public void setCapital(List<CapitalSpec> capital) {
        this.capital = capital;
    }
}
