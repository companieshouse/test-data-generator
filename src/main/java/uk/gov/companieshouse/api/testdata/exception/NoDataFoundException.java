package uk.gov.companieshouse.api.testdata.exception;

public class NoDataFoundException extends Exception {

    private static final long serialVersionUID = 8923487504100307810L;

    public NoDataFoundException(String message) {
        super(message);
    }
}
