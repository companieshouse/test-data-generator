package uk.gov.companieshouse.api.testdata.service;

import java.util.List;
import uk.gov.companieshouse.api.testdata.model.entity.Postcodes;

public interface PostcodeService {
    /**
     * Retrieves postcodes for a given country.
     *
     * @param country the country name which to retrieve postcodes for
     * @return Postcodes object containing the post codes for the specified country
     */
    List<Postcodes> getPostcodeByCountry(String country);
}
