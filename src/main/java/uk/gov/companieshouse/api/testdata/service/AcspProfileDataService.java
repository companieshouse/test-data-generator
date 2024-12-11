package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspProfileSpec;

public interface AcspProfileDataService<T> {
    T create(AcspProfileSpec acspProfileSpec) throws DataException;

    /**
     * Delete information for the given {@code acspNumber}
     *
     * @param acspNumber
     * @return True if the data could be found and deleted. False if no data found
     */
    boolean delete(String acspNumber);
}
