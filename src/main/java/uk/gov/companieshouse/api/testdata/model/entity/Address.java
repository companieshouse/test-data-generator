package uk.gov.companieshouse.api.testdata.model.entity;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Objects;

public class Address {
    @Field("premise")
    private final String premise;
    @Field("address_line_1")
    private final String addressLine1;
    @Field("address_line_2")
    private final String addressLine2;
    @Field("country")
    private final String country;
    @Field("locality")
    private final String locality;
    @Field("postal_code")
    private final String postalCode;

    public Address(String premise, String addressLine1, String addressLine2, String country, String locality, String postalCode) {
        this.premise = premise;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.locality = locality;
        this.postalCode = postalCode;
    }

    public String getPremise() {
        return premise;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getCountry() {
        return country;
    }

    public String getLocality() {
        return locality;
    }

    public String getPostalCode() {
        return postalCode;
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
        return Objects.equals(getPremise(), address.getPremise()) &&
                Objects.equals(getAddressLine1(), address.getAddressLine1()) &&
                Objects.equals(getAddressLine2(), address.getAddressLine2()) &&
                Objects.equals(getCountry(), address.getCountry()) &&
                Objects.equals(getLocality(), address.getLocality()) &&
                Objects.equals(getPostalCode(), address.getPostalCode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPremise(), getAddressLine1(), getAddressLine2(), getCountry(), getLocality(), getPostalCode());
    }
}
