package uk.gov.companieshouse.api.testdata.service.address.profile;

import net.datafaker.Faker;

/**
 * Represents a non-EU country profile for address generation.
 * Contains country name, faker instance, and locality clusters.
 */
public class NonEuProfile {
    public final String country;
    public final Faker faker;
    public final LocalityCluster[] clusters;

    public NonEuProfile(String country, Faker faker, LocalityCluster[] clusters) {
        this.country = country;
        this.faker = faker;
        this.clusters = clusters;
    }
}
