package uk.gov.companieshouse.api.testdata.model.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "combined_sic_activities")
public class CombinedSicActivities {
  @Id
  @Field("_id")
  private ObjectId id;

  @Field("sic_code")
  private String sicCode;

  @Field("activity_description")
  private String activityDescription;

  @Field("sic_description")
  private String sicDescription;

  @Field("is_ch_activity")
  private boolean isChActivity;

  @Field("activity_description_search_field")
  private String activityDescriptionSearchField;

  public ObjectId getId() {
    return id;
  }

  public void setId(ObjectId id) {
    this.id = id;
  }

  public String getSicCode() {
    return sicCode;
  }

  public void setSicCode(String sicCode) {
    this.sicCode = sicCode;
  }

  public String getActivityDescription() {
    return activityDescription;
  }

  public void setActivityDescription(String activityDescription) {
    this.activityDescription = activityDescription;
  }

  public String getSicDescription() {
    return sicDescription;
  }

  public void setSicDescription(String sicDescription){
    this.sicDescription = sicDescription;
  }

  public Boolean getIsChActivity() {
    return isChActivity;
  }

  public void setIsChActivity(Boolean isChActivity) {
    this.isChActivity = isChActivity;
  }

  public String getActivityDescriptionSearchField() {
    return activityDescriptionSearchField;
  }

  public void setActivityDescriptionSearchField(String activityDescriptionSearchField) {
    this.activityDescriptionSearchField = activityDescriptionSearchField;
  }
}
