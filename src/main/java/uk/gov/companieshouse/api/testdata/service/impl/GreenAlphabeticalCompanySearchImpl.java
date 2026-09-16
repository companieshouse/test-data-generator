package uk.gov.companieshouse.api.testdata.service.impl;

import java.util.function.Supplier;

import org.springframework.stereotype.Service;
import uk.gov.companieshouse.api.InternalApiClient;

@Service("greenAlphabeticalCompanySearchService")
public class GreenAlphabeticalCompanySearchImpl extends AlphabeticalCompanySearchImpl {
    public GreenAlphabeticalCompanySearchImpl(Supplier<InternalApiClient> internalApiClientSupplier) {
        super(internalApiClientSupplier);
        this.instance = "/green";
    }
}
