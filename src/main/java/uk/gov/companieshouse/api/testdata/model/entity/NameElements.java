package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.StringJoiner;

public class NameElements {

    @Field("forename")
    private String forename;
    @Field("otherForenames")
    private String otherForenames;
    @Field("surname")
    private String surname;
    @Field("title")
    private String title;

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getOtherForenames() {
        return otherForenames;
    }

    public void setOtherForenames(String otherForenames) {
        this.otherForenames = otherForenames;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return new StringJoiner(" ", "", "")
                .add(title)
                .add(forename)
                .add(otherForenames)
                .add(surname)
                .toString();
    }
}
