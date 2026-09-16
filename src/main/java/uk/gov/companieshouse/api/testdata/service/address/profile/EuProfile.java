package uk.gov.companieshouse.api.testdata.service.address.profile;

import net.datafaker.Faker;

/**
 * Represents an EU country profile for address generation.
 * Contains country name, faker instance, locality clusters, and postcode type.
 */
public class EuProfile {
    public final String country;
    public final Faker faker;
    public final LocalityCluster[] clusters;
    public final EuPostcodeType postcodeType;

    public EuProfile(String country, Faker faker, LocalityCluster[] clusters, EuPostcodeType postcodeType) {
        this.country = country;
        this.faker = faker;
        this.clusters = clusters;
        this.postcodeType = postcodeType;
    }

    public enum EuPostcodeType {
        NETHERLANDS,
        GERMANY,
        FRANCE,
        SPAIN,
        ITALY,
        POLAND
    }
}
