package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;

public interface AcspDataService<T> {
    T create(AcspSpec acspSpec) throws DataException;

    /**
     * Delete information for the given {@code companyNymber}
     *
     * @param acspNumber
     * @return True if the data could be found and deleted. False if no data found
     */
    boolean delete(long acspNumber);

}
