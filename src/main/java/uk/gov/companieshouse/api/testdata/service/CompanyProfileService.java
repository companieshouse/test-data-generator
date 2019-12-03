package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;

public interface CompanyProfileService extends DataService<CompanyProfile> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     * 
     * @param companyNumber
     * @return True if a company exists. False otherwise
     */
    boolean companyExists(String companyNumber);
}
