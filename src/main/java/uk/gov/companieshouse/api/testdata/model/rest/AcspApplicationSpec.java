package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
public class AcspApplicationSpec {

    @JsonProperty
    private String id;

    @JsonProperty
    private String typeOfBusiness;

    @JsonProperty("user_id")
    private String user_id;

    @JsonProperty
    private String self;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTypeOfBusiness(){return typeOfBusiness;}
    public void setTypeOfBusiness(String typeOfBusiness){this.typeOfBusiness=typeOfBusiness;}

    public String getUser_id(){return user_id;}
    public void setUser_id(String user_id){this.user_id=user_id;}
    public String getSelf(){return self;}
    public void setSelf(String self){this.self=self;}
}
