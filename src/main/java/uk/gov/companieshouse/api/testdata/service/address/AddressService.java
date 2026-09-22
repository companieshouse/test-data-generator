package uk.gov.companieshouse.api.testdata.service.address;

import uk.gov.companieshouse.api.testdata.model.entity.Address;
import uk.gov.companieshouse.api.testdata.model.entity.UsualResidentialAddress;
import uk.gov.companieshouse.api.testdata.model.rest.enums.JurisdictionType;

public interface AddressService {
    /**
     * Returns the companies house office address for the given {@code jurisdiction}
     * @param jurisdiction jurisdiction of the address to return
     * @return the companies house office address in given {@code jurisdiction}
     */
    Address getAddress(JurisdictionType jurisdiction);
    Address getOverseasAddress();

    /**
     * Returns the country name from the currently selected profile for the jurisdiction.
     * If no profile is cached, returns the default country for the jurisdiction.
     * @param jurisdiction jurisdiction to get the country for
     * @return the country name from the cached profile or default
     */
    String getCountryFromSelectedProfile(JurisdictionType jurisdiction);

    UsualResidentialAddress getUsualResidentialAddress();
    UsualResidentialAddress getUsualResidentialAddress(JurisdictionType jurisdiction);
}
