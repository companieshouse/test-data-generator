package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

public class Address {

    @Field("address_line_1")
    private String addressLine1;
    @Field("address_line_2")
    private String addressLine2;
    @Field("country")
    private String country;
    @Field("locality")
    private String locality;
    @Field("postal_code")
    private String postalCode;

    public Address() {
    }

    public Address(String addressLine1, String addressLine2, String country, String locality, String postalCode) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.locality = locality;
        this.postalCode = postalCode;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Address address = (Address) o;
        return Objects.equals(getAddressLine1(), address.getAddressLine1()) &&
                Objects.equals(getAddressLine2(), address.getAddressLine2()) &&
                Objects.equals(getCountry(), address.getCountry()) &&
                Objects.equals(getLocality(), address.getLocality()) &&
                Objects.equals(getPostalCode(), address.getPostalCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAddressLine1(), getAddressLine2(), getCountry(), getLocality(), getPostalCode());
    }
}
