package uk.gov.companieshouse.api.testdata.model.entity;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class FilingHistoryDescriptionValues {

    @Field("date")
    private String date;

    @Field("capital")
    private List<Capital> capital;

    @Field("charge_number")
    private String chargeNumber;

    public String getDate() { return date; }

    public void setDate(String date) {
      this.date = date;
    }

    public List<Capital> getCapital() { return capital; }

    public void setCapital(List<Capital> capital) {
      this.capital = capital;
    }

    public String getChargeNumber() { return chargeNumber; }

    public void setChargeNumber(String chargeNumber) {
        this.chargeNumber = chargeNumber;
    }
}
