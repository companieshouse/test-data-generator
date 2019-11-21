package uk.gov.companieshouse.api.testdata.exception;

public class BarcodeServiceException extends Exception {

    private static final long serialVersionUID = 8972556655455615862L;

    public BarcodeServiceException(String message) {
        super(message);
    }

    public BarcodeServiceException(Throwable cause) {
        super(cause);
    }

    public BarcodeServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
