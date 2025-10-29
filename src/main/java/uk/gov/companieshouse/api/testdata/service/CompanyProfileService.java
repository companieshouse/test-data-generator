package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;

import java.util.List;

public interface CompanyProfileService extends DataService<CompanyProfile, CompanySpec> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     * 
     * @param companyNumber The company number to check
     * @return True if a company exists. False otherwise
     */
    boolean companyExists(String companyNumber);

    List<String> findUkEstablishmentsByParent(String parentCompanyNumber);

    CompanyProfile getCompanyProfile(String companyNumber);
}
