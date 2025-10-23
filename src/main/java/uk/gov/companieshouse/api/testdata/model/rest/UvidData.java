package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UvidData {

    @JsonProperty("id")
    private String id;

    @JsonProperty("uvid")
    private String uvid;

    public UvidData(){

    }

    public UvidData(String id, String uvid ) {
        this.id = id;
        this.uvid = uvid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUvid() {
        return uvid;
    }

    public void setUvid(String uvid) {
        this.uvid = uvid;
    }
}
