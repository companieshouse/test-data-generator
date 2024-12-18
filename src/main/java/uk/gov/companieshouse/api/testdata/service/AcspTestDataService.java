package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.AcspData;
import uk.gov.companieshouse.api.testdata.model.rest.AcspSpec;

public interface AcspTestDataService {
    /**
     * Create company data with given {@code companySpec}
     *
     * @param acspSpec The specification the new acsp profile must adhere to
     * @return A {@link AcspData}
     * @throws DataException If any error occurs
     */
    AcspData createAcspData(AcspSpec acspSpec) throws DataException;

    /**
     * Delete all data for acsp {@code acspNumber}
     *
     * @param acspNumber The acsp number to be deleted
     * @throws DataException If any error occurs
     */
    void deleteAcspData(long acspNumber) throws DataException;
}
