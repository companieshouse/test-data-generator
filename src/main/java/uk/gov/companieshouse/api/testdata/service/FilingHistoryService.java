package uk.gov.companieshouse.api.testdata.service;

import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.entity.FilingHistory;

public interface FilingHistoryService {
    /**
     * Retrieves company filing histories for a given company number.
     *
     * @param companyNumber the company number which to retrieve filing histories for
     * @return FilingHistory object containing the filing histories for the specified company number
     */
    List<FilingHistory> getCompanyFilingHistoryByCompanyNumber(String companyNumber);

    /**
     * Retrieves company filing history for a given id.
     *
     * @param id the id which to retrieve filing historu for
     * @return FilingHistory object containing the filing history for the specified id
     */
    Optional<FilingHistory> getCompanyFilingHistoryById(String id);

    /**
     * Deletes a filing history by its company number.
     *
     * @param companyNumber the company number
     * @return the {@link ResponseEntity} with the HTTP status
     * @throws NoDataFoundException if the filing history cannot be found
     * @throws DataException        if the filing history failed to be deleted
     */
    boolean deleteCompanyFilingHistory(String companyNumber)
            throws NoDataFoundException;
}
