package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public class CombinedSicActivitiesSpec {
  @JsonProperty("activity_description")
  @NotEmpty(message = "Activity description is required")
  private String activityDescription;

  @JsonProperty("sic_description")
  @NotEmpty(message = "Sic description is required")
  private String sicDescription;

  @JsonProperty("is_ch_activity")
  @NotNull(message = "Ch Activity is required")
  private Boolean isChActivity;

  @JsonProperty("activity_description_search_field")
  @NotEmpty(message = "Activity description search field is required")
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
