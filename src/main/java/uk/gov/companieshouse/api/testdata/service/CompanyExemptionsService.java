package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.CompanyExemptionsRequest;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyExemptionsResponse;

public interface CompanyExemptionsService {

    /**
     * Creates a new company exemptions entry or updates an existing one. If the company number
     * already exists, the exemptions data and timestamps are updated while preserving the
     * original created timestamp.
     *
     * @param request the company exemptions request containing company_number and optional
     *                exemption_type and data
     * @return the created or updated {@link CompanyExemptionsResponse}
     * @throws DataException if an error occurs during creation or update
     */
    CompanyExemptionsResponse createOrUpdate(CompanyExemptionsRequest request) throws DataException;

    /**
     * Deletes a company exemptions entry by its company number.
     *
     * @param companyNumber the company number
     * @return true if the exemption was deleted, false if not found
     */
    boolean deleteByCompanyNumber(String companyNumber);
}