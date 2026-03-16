package uk.gov.companieshouse.api.testdata.service;

import java.util.Optional;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.AcspProfile;
import uk.gov.companieshouse.api.testdata.model.rest.response.AcspProfileResponse;
import uk.gov.companieshouse.api.testdata.model.rest.request.AcspProfileRequest;

public interface AcspProfileService extends
        DataService<AcspProfileResponse, AcspProfileRequest> {

    /**
     * Checks whether a company with the given {@code companyNumber} is present
     *
     * @param acspNumber The company number to check
     * @return True if a company exists. False otherwise
     */

    Optional<AcspProfile> getAcspProfile(String acspNumber) throws NoDataFoundException;
}
