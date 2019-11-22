package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.rest.Jurisdiction;

public interface AddressService {
    /**
     * Returns the companies house office address for the given {@code jurisdiction}
     * @param jurisdiction jurisdiction of the address to return
     * @return the companies house office address in given {@code jurisdiction}
     */
    Address getAddressForJurisdiction(Jurisdiction jurisdiction);
}
