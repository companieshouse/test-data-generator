package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "acsp_application")
public class AcspApplication {

    @Id
    @Field("_id")
    private String id;

    @Field("data.type_of_business")
    private String typeOfBusiness;

    @Field("acsp_data_submission.last_modified_by_user_id")
    private String user_id;

    @Field("links.self")
    private String self;
    public String getId() {
        return id;
    }

    public String getTypeOfBusiness(){
        return typeOfBusiness;
    }
    public String getUser_id(){
        return user_id;
    }

    public String getSelf(){
        return self;
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setTypeOfBusiness(String typeOfBusiness){
        this.typeOfBusiness=typeOfBusiness;
    }

    public void setUser_id(String user_id){
        this.user_id=user_id;
    }

    public void setSelf(String self){
        this.self=self;
    }

}