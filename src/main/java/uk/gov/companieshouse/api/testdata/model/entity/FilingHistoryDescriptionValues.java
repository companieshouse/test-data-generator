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

    @Field("made_up_date")
    private String madeUpDate;

    @Field("officer_name")
    private String officerName;

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

    public String getMadeUpDate() { return madeUpDate; }

    public void setMadeUpDate(String madeUpDate) {
        this.madeUpDate = madeUpDate;
    }

    public String getOfficerName() { return officerName; }

    public void setOfficerName(String officerName) {
        this.officerName = officerName;
    }
}
