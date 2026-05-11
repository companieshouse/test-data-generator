package uk.gov.companieshouse.api.testdata.exception;

public class PostcodeLoadException extends RuntimeException {

    public PostcodeLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
