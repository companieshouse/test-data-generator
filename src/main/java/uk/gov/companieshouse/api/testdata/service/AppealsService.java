package uk.gov.companieshouse.api.testdata.service;

public interface AppealsService {
    /**
     * Deletes an entity by its penalty reference and company number.
     *
     * @param companyNumber the company number
     * @param penaltyReference the penalty reference
     * @return true if the entity was deleted, false otherwise
     */
    boolean delete(String companyNumber, String penaltyReference);
}
