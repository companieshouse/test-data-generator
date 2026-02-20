package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
public class AcspApplicationRequest {

    @JsonProperty
    private String id;

    @JsonProperty
    private String typeOfBusiness;

    @JsonProperty("user_id")
    private String userId;

    @JsonProperty
    private String self;


    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getUserId(){return userId;}
    public void setUserId(String userId){this.userId=userId;}

}
