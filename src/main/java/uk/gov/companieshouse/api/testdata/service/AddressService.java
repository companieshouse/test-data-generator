package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

public interface AddressService {
    /**
     * Returns the companies house office address for the given {@code jurisdiction}
     * @param jurisdiction jurisdiction of the address to return
     * @return the companies house office address in given {@code jurisdiction}
     */
    Address getAddress(Jurisdiction jurisdiction);

    /**
     * Returns the country of residence string for the given {@code jurisdiction}
     * @param jurisdiction jurisdiction of the country of residence to return
     * @return the country of residence for the given {@code jurisdiction}
     */
    String getCountryOfResidence(Jurisdiction jurisdiction);
}
