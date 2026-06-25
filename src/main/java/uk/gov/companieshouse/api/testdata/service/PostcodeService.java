package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.response.PostcodesResponse;

public interface PostcodeService {
    /**
     * Retrieves postcodes for a given country.
     *
     * @param country the country for which to retrieve postcodes
     * @return Postcodes object containing the postcodes for the specified country
     * @throws DataException if there is an error retrieving the postcodes
     */
    PostcodesResponse getPostcodes(String country) throws DataException;

}
