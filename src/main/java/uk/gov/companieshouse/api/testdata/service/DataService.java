package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

public interface DataService<T> {
    T create(CompanySpec companySpec) throws DataException;

    /**
     * Delete information for the given {@code companyNymber}
     * 
     * @param companyNumber
     * @return True if the data could be found and deleted. False if no data found
     */
    boolean delete(String companyNumber);
}
