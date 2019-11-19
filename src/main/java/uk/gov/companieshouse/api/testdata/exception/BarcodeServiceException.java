package uk.gov.companieshouse.api.testdata.exception;

public class BarcodeServiceException extends Exception {

    private static final long serialVersionUID = -7421602790483260597L;

    public BarcodeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
