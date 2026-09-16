package uk.gov.companieshouse.api.testdata.service.address.profile;

/**
 * Represents a locality cluster with locality name, area, and region.
 */
public class LocalityCluster {
    public final String locality;
    public final String area;
    public final String region;

    public LocalityCluster(String locality, String area, String region) {
        this.locality = locality;
        this.area = area;
        this.region = region;
    }
}
