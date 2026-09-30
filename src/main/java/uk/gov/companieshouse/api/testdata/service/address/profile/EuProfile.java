package uk.gov.companieshouse.api.testdata.service.address.profile;

import net.datafaker.Faker;

/**
 * Represents an EU country profile for address generation.
 * Contains country name, faker instance, and locality clusters.
 */
public class EuProfile {
    public final String country;
    public final Faker faker;
    public final LocalityCluster[] clusters;

    public EuProfile(String country, Faker faker, LocalityCluster[] clusters) {
        this.country = country;
        this.faker = faker;
        this.clusters = clusters;
    }
}
