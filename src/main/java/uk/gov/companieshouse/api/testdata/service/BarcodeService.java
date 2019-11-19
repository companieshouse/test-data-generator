package uk.gov.companieshouse.api.testdata.service;

import uk.gov.companieshouse.api.testdata.exception.BarcodeServiceException;

public interface BarcodeService {
    String getBarcode() throws BarcodeServiceException;
}
