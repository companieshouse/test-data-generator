package uk.gov.companieshouse.api.testdata.service.impl;

import uk.gov.companieshouse.api.company.CompanyProfile;
import uk.gov.companieshouse.api.error.ApiErrorResponseException;
import uk.gov.companieshouse.api.handler.Executor;
import uk.gov.companieshouse.api.handler.ResourceHandler;
import uk.gov.companieshouse.api.handler.company.request.PrivateCompanyURIPattern;
import uk.gov.companieshouse.api.handler.exception.URIValidationException;
import uk.gov.companieshouse.api.handler.regex.URIValidator;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.model.ApiResponse;
import uk.gov.companieshouse.api.request.RequestExecutor;

public class PrivateCompanyProfilePut extends ResourceHandler implements Executor<ApiResponse<Void>> {

    private CompanyProfile companyProfile;

    public PrivateCompanyProfilePut(HttpClient httpClient, String url, String uri,
                                    RequestExecutor requestExecutor, CompanyProfile companyProfile) {
        super(httpClient, url, uri, requestExecutor);
        this.companyProfile = companyProfile;
    }

    @Override
    public ApiResponse<Void> execute() throws URIValidationException,
            ApiErrorResponseException {
        if (!URIValidator.validate(PrivateCompanyURIPattern.patchPattern(), uri)) {
            throw new URIValidationException(INVALID_URI);
        }
        return requestExecutor.putRequest(httpClient, url, uri, companyProfile,null);
    }
}
