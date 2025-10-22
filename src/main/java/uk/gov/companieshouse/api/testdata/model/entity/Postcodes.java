package uk.gov.companieshouse.api.testdata.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Postcodes {
    @JsonProperty("address_key")
    private String addressKey;

    @JsonProperty("number_of_households")
    private int numberOfHouseholds;

    @JsonProperty("country")
    private String country;

    @JsonProperty("building_number")
    private Integer buildingNumber;

    @JsonProperty("building_name")
    private String buildingName;

    @JsonProperty("sub_building_name")
    private String subBuildingName;

    @JsonProperty("po_box_number")
    private String poBoxNumber;

    @JsonProperty("postcode")
    private PostcodeDetails postcode;

    @JsonProperty("organisation")
    private Organisation organisation;

    @JsonProperty("locality")
    private Locality locality;

    @JsonProperty("thoroughfare")
    private Thoroughfare thoroughfare;

    @JsonProperty("dependent_thoroughfare")
    private Thoroughfare dependentThoroughfare;

    public String getAddressKey() {
        return addressKey;
    }

    public void setAddressKey(String addressKey) {
        this.addressKey = addressKey;
    }

    public int getNumberOfHouseholds() {
        return numberOfHouseholds;
    }

    public void setNumberOfHouseholds(int numberOfHouseholds) {
        this.numberOfHouseholds = numberOfHouseholds;
    }

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

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public String getSubBuildingName() {
        return subBuildingName;
    }

    public void setSubBuildingName(String subBuildingName) {
        this.subBuildingName = subBuildingName;
    }

    public String getPoBoxNumber() {
        return poBoxNumber;
    }

    public void setPoBoxNumber(String poBoxNumber) {
        this.poBoxNumber = poBoxNumber;
    }

    public PostcodeDetails getPostcode() {
        return postcode;
    }

    public void setPostcode(PostcodeDetails postcode) {
        this.postcode = postcode;
    }

    public Organisation getOrganisation() {
        return organisation;
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
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

    public static class Organisation {
        @JsonProperty("key")
        private String key;
        @JsonProperty("name")
        private String name;
        @JsonProperty("postcode_type")
        private String postcodeType;
        @JsonProperty("department_name")
        private String departmentName;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPostcodeType() {
            return postcodeType;
        }

        public void setPostcodeType(String postcodeType) {
            this.postcodeType = postcodeType;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }


    }

    public static class Locality {
        @JsonProperty("post_town")
        private String postTown;
        @JsonProperty("dependent_locality")
        private String dependentLocality;
        @JsonProperty("double_dependent_locality")
        private String doubleDependentLocality;

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

        public String getDoubleDependentLocality() {
            return doubleDependentLocality;
        }

        public void setDoubleDependentLocality(String doubleDependentLocality) {
            this.doubleDependentLocality = doubleDependentLocality;
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