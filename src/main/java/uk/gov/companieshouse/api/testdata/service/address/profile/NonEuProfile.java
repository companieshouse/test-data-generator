package uk.gov.companieshouse.api.testdata.service.address.profile;

import net.datafaker.Faker;

/**
 * Represents a non-EU country profile for address generation.
 * Contains country name, faker instance, locality clusters, and postcode type.
 */
public class NonEuProfile {
    public final String country;
    public final Faker faker;
    public final LocalityCluster[] clusters;
    public final NonEuPostcodeType postcodeType;

    public NonEuProfile(String country, Faker faker, LocalityCluster[] clusters, NonEuPostcodeType postcodeType) {
        this.country = country;
        this.faker = faker;
        this.clusters = clusters;
        this.postcodeType = postcodeType;
    }

    public enum NonEuPostcodeType {
        PANAMA,
        CANADA,
        AUSTRALIA,
        JERSEY,
        MALTA,
        CYPRUS,
        BERMUDA
    }
}
