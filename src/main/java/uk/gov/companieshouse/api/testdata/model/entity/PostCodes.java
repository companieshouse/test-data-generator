package uk.gov.companieshouse.api.testdata.model.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "postcodes")
public class PostCodes {
    @Id
    @Field("_id")
    private ObjectId id;

    @Field("postcode.type")
    private String postcode;

    @Field("postcode.stripped")
    private String stripped;

    @Field("postcode.pretty")
    private String pretty;

    @Field("building_number")
    private Number buildingNumber;

    @Field("thoroughfare.name")
    private String thoroughfareName;

    @Field("thoroughfare.descriptor")
    private String thoroughfareDescriptor;

    @Field("organisation.key")
    private String organisationKey;

    @Field("locality.post_town")
    private String localityPostTown;

    @Field("locality.dependent_locality")
    private String dependentLocality;

    @Field("country")
    private String country;

    @Field("number_of_households")
    private Float numberOfHouseholds;

    @Field("address_key")
    private String addressKey;

    public ObjectId getId() {
        return id;
    }

    public String getPretty() {
        return pretty;
    }

    public Number getBuildingNumber() {
        return buildingNumber;
    }

    public String getThoroughfareName() {
        return thoroughfareName;
    }

    public String getThoroughfareDescriptor() {
        return thoroughfareDescriptor;
    }

    public String getLocalityPostTown() {
        return localityPostTown;
    }

    public String getCountry() {
        return country;
    }

    public String getDependentLocality() {
        return dependentLocality;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setPretty(String pretty) {
        this.pretty = pretty;
    }

    public void setBuildingNumber(Number buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public void setThoroughfareName(String thoroughfareName) {
        this.thoroughfareName = thoroughfareName;
    }

    public void setThoroughfareDescriptor(String thoroughfareDescriptor) {
        this.thoroughfareDescriptor = thoroughfareDescriptor;
    }

    public void setLocalityPostTown(String localityPostTown) {
        this.localityPostTown = localityPostTown;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setDependentLocality(String dependentLocality) {
        this.dependentLocality = dependentLocality;
    }
}
