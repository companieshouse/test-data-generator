package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.CompanyAuthCode;

public interface CompanyAuthCodeService extends DataService<CompanyAuthCode>{

    /**
     * Verify the given {@code authCode} against the company with {@code
     * companyNumber}
     * 
     * @param companyNumber The company number for the company we are verifying the
     *                      authorisation code
     * @param authCode      The authorisation code to be verified
     * @return True if the {@code authCode} matches the one of the given company.
     *         False if it does not match.
     * @throws NoDataFoundException If no authorisation code was found for the given
     *                              {@code companyNumber}
     */
    boolean verifyAuthCode(String companyNumber, String authCode) throws NoDataFoundException;

}
