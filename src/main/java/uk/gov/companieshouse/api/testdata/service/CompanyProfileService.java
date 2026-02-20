package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;

import java.util.List;
import java.util.Optional;

public interface CompanyProfileService extends DataService<CompanyProfile, CompanyRequest> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     * 
     * @param companyNumber The company number to check
     * @return True if a company exists. False otherwise
     */
    boolean companyExists(String companyNumber);

    List<String> findUkEstablishmentsByParent(String parentCompanyNumber);

    Optional<CompanyProfile> getCompanyProfile(String companyNumber);
}
