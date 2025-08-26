package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CombinedSicActivitiesSpec {
  @JsonProperty("activity_description")
  private String activityDescription;

  @JsonProperty("sic_description")
  private String sicDescription;

  @JsonProperty("is_ch_activity")
  private boolean isChActivity;

  @JsonProperty("activity_description_search_field")
  private String activityDescriptionSearchField;

  public String getActivityDescription() {
    return activityDescription;
  }

  public void setActivityDescription(String activityDescription) {
    this.activityDescription = activityDescription;
  }

  public String getSicDescription() {
    return sicDescription;
  }

  public void setSicDescription(String sicDescription) {
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
