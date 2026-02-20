package uk.gov.companieshouse.api.testdata.model.rest.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CombinedSicActivitiesResponse {

  @JsonProperty("_id")
  private final String id;

  @JsonProperty("sic_code")
  private final String sicCode;

  @JsonProperty("sic_description")
  private final String sicDescription;

  public CombinedSicActivitiesResponse(String id, String sicCode, String sicDescription) {
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
