package uk.gov.companieshouse.api.testdata.service.impl;

import uk.gov.companieshouse.api.company.CompanyProfile;
import uk.gov.companieshouse.api.http.HttpClient;
import uk.gov.companieshouse.api.request.RequestExecutor;

public class PrivateCompanyProfileHandler {
    private final HttpClient httpClient;
    private final String url;
    private final RequestExecutor requestExecutor;

    public PrivateCompanyProfileHandler(HttpClient httpClient, String url,
                                        RequestExecutor requestExecutor) {
        this.httpClient = httpClient;
        this.url = url;
        this.requestExecutor = requestExecutor;
    }

    public PrivateCompanyProfilePut patchCompanyProfile(String uri, CompanyProfile companyProfile) {
        return new PrivateCompanyProfilePut(this.httpClient, this.url, uri,
                this.requestExecutor, companyProfile);
    }
}
