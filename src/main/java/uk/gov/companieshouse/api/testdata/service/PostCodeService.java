package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.PostCodes;

import java.util.List;

public interface PostCodeService {
    /**
     * Retrieves post codes for a given country.
     *
     * @param country the country for which to retrieve post codes
     * @return PostCodes object containing the post codes for the specified country
     */
    List<PostCodes> get(String country);
}
