package uk.gov.companieshouse.api.testdata.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;
import uk.gov.companieshouse.sdk.manager.ApiSdkManager;

@Service
public class AccountsApiService {

    private final String privateAccountUrl;

    public AccountsApiService(@Value("${account.api.url}") String privateAccountUrl) {
        this.privateAccountUrl = privateAccountUrl;
    }

    public InternalApiClient getInternalApiClientForPrivateAccountApiUrl() {
        final var internalApiClient = ApiSdkManager.getInternalSDK();
        internalApiClient.setInternalBasePath(privateAccountUrl);
        return internalApiClient;
    }
}