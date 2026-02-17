package uk.gov.companieshouse.api.testdata.exception;

public class PostcodeServiceException extends RuntimeException {

    public PostcodeServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public PostcodeServiceException(String message) {
        super(message);
    }

    public PostcodeServiceException(Throwable cause) {
        super(cause);
    }

}
