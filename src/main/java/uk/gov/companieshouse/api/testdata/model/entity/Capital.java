package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

public class Capital {

    @Field("figure")
    private String figure;

    @Field("currency")
    private String currency;

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
