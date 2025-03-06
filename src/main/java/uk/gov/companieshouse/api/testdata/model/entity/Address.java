package uk.gov.companieshouse.api.testdata.model.entity;

import java.beans.ConstructorProperties;
import java.util.Objects;
import org.springframework.data.mongodb.core.mapping.Field;

public class Address {
    @Field("premise")
    private String premise;
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
        this("", "", "", "", "", "");
    }

    @ConstructorProperties({
            "premise", "address_line_1", "address_line_2", "country", "locality", "postal_code"})
    public Address(String premise,
                   String addressLine1,
                   String addressLine2,
                   String country,
                   String locality,
                   String postalCode) {
        this.premise = premise;
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.country = country;
        this.locality = locality;
        this.postalCode = postalCode;
    }

    public Address(String addressLine1,
                   String addressLine2,
                   String country,
                   String locality,
                   String postalCode) {
        this("", addressLine1, addressLine2, country, locality, postalCode);
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
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Address)) {
            return false;
        }
        var address = (Address) obj;
        return Objects.equals(premise, address.premise)
                && Objects.equals(addressLine1, address.addressLine1)
                && Objects.equals(addressLine2, address.addressLine2)
                && Objects.equals(country, address.country)
                && Objects.equals(locality, address.locality)
                && Objects.equals(postalCode, address.postalCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(premise, addressLine1, addressLine2, country, locality, postalCode);
    }
}