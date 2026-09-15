package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;
import uk.gov.companieshouse.api.testdata.model.rest.response.CompanyProfileResponse;
import uk.gov.companieshouse.api.testdata.service.CompanySearchService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Service("greenAlphabeticalCompanySearchService")
public class GreenAlphabeticalCompanySearchImpl extends AlphabeticalCompanySearchImpl {
    public GreenAlphabeticalCompanySearchImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        super(internalApiClientSupplier);
        this.instance = "/green";
    }
}
