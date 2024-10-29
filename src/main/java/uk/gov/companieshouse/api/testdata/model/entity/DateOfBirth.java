package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;
import java.util.Objects;

public class DateOfBirth {
    @Field("day")
    private Integer day;
    @Field("month")
    private Integer month;
    @Field("year")
    private Integer year;

    public DateOfBirth(int day, int month, int year){
        this.day = day;
        this.month = month;
        this.year = year;
    }

    public Integer getDay() { return day; }

    public Integer getMonth() { return month; }

    public Integer getYear() { return year; }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        DateOfBirth that = (DateOfBirth) object;
        return Objects.equals(day, that.day)
                && Objects.equals(month, that.month)
                && Objects.equals(year, that.year);
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

}