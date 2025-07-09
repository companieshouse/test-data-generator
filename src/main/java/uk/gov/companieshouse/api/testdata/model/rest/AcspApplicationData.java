package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AcspApplicationData {

    @JsonProperty("_id")
    private final String id;

    @JsonProperty("type_of_business")
    private final String typeOfBusiness;

    @JsonProperty("last_modified_by_user_id")
    private final String user_id;

    @JsonProperty("self")
    private final String self;

    public AcspApplicationData(String id, String typeOfBusiness,String user_id,String self) {
        this.id = id;
        this.typeOfBusiness = typeOfBusiness;
        this.user_id=user_id;
        this.self=self;
    }

    public String getId() {
        return id;
    }

    public String getTypeOfBusiness() {
        return typeOfBusiness;
    }

    public String getUser_id(){return user_id;}
    public String getSelf(){return self;}
}
