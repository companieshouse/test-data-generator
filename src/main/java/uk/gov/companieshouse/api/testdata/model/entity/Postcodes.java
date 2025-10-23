package uk.gov.companieshouse.api.testdata.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Postcodes {

    @JsonProperty("country")
    private String country;

    @JsonProperty("building_number")
    private Integer buildingNumber;

    @JsonProperty("postcode")
    private PostcodeDetails postcode;

    @JsonProperty("locality")
    private Locality locality;

    @JsonProperty("thoroughfare")
    private Thoroughfare thoroughfare;

    @JsonProperty("dependent_thoroughfare")
    private Thoroughfare dependentThoroughfare;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getBuildingNumber() {
        return buildingNumber;
    }

    public void setBuildingNumber(Integer buildingNumber) {
        this.buildingNumber = buildingNumber;
    }

    public PostcodeDetails getPostcode() {
        return postcode;
    }

    public void setPostcode(PostcodeDetails postcode) {
        this.postcode = postcode;
    }

    public Locality getLocality() {
        return locality;
    }

    public void setLocality(Locality locality) {
        this.locality = locality;
    }

    public Thoroughfare getThoroughfare() {
        return thoroughfare;
    }

    public void setThoroughfare(Thoroughfare thoroughfare) {
        this.thoroughfare = thoroughfare;
    }

    public Thoroughfare getDependentThoroughfare() {
        return dependentThoroughfare;
    }

    public void setDependentThoroughfare(Thoroughfare dependentThoroughfare) {
        this.dependentThoroughfare = dependentThoroughfare;
    }


    public static class PostcodeDetails {
        @JsonProperty("pretty")
        private String pretty;

        @JsonProperty("stripped")
        private String stripped;

        @JsonProperty("type")
        private String type;

        public String getPretty() {
            return pretty;
        }

        public void setPretty(String pretty) {
            this.pretty = pretty;
        }

        public String getStripped() {
            return stripped;
        }

        public void setStripped(String stripped) {
            this.stripped = stripped;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Locality {
        @JsonProperty("post_town")
        private String postTown;

        @JsonProperty("dependent_locality")
        private String dependentLocality;

        public String getPostTown() {
            return postTown;
        }

        public void setPostTown(String postTown) {
            this.postTown = postTown;
        }

        public String getDependentLocality() {
            return dependentLocality;
        }

        public void setDependentLocality(String dependentLocality) {
            this.dependentLocality = dependentLocality;
        }
    }

    public static class Thoroughfare {
        @JsonProperty("name")
        private String name;

        @JsonProperty("descriptor")
        private String descriptor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescriptor() {
            return descriptor;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }
    }
}