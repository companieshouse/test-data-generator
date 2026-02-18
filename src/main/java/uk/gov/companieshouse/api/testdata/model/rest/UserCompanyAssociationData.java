package uk.gov.companieshouse.api.testdata.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCompanyAssociationData {
    private String id;
    @JsonProperty("association_link")
    private String associationLink;

    public UserCompanyAssociationData() {}

    public UserCompanyAssociationData(String id, String associationLink) {
        this.id = id;
        this.associationLink = associationLink;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssociationLink() {
        return associationLink;
    }

    public void setAssociationLink(String associationLink) {
        this.associationLink = associationLink;
    }
}