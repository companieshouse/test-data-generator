package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyProfile;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest;

import java.util.List;
import java.util.Optional;
import uk.gov.companieshouse.api.testdata.model.rest.request.UpdateCompanyRequest;

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

    /**
     * Updates an company profile for the company Number.
     * *
     * @param request the update request with company Number as mandatory.
     * @throws NoDataFoundException if the company number cannot be found
     * @throws DataException if the company number details cannot be updated
     */
    CompanyProfile updateCompany(
            UpdateCompanyRequest request) throws NoDataFoundException, DataException;

}
