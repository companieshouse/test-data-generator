package uk.gov.companieshouse.api.testdata.model.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
public class CertificatesRequest extends AbstractCompanyBasketRequest {

    @JsonProperty("description")
    private String description;

    @JsonProperty("description_identifier")
    private String descriptionIdentifier;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescriptionIdentifier() {
        return descriptionIdentifier;
    }

    public void setDescriptionIdentifier(String descriptionIdentifier) {
        this.descriptionIdentifier = descriptionIdentifier;
    }
}
