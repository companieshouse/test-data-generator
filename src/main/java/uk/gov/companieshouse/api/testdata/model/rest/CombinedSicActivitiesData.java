package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;

public class CombinedSicActivitiesData {

  @JsonProperty("_id")
  private final String id;

  @JsonProperty("sic_code")
  private final String sicCode;

  @JsonProperty("sic_description")
  private final String sicDescription;

  public CombinedSicActivitiesData(String id, String sicCode, String sicDescription) {
    this.id = id;
    this.sicCode = sicCode;
    this.sicDescription = sicDescription;
  }

  public String getId() {
    return id;
  }

  public String getSicCode() {
    return sicCode;
  }

  public String getSicDescription() {
    return sicDescription;
  }
}
