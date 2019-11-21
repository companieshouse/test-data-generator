package uk.gov.companieshouse.api.testdata.exception;

public class DataException extends Exception {

    private static final long serialVersionUID = 6534913251044629837L;

    public DataException(String message) {
        super(message);
    }

    public DataException(Throwable cause) {
        super(cause);
    }

    public DataException(String message, Throwable cause) {
        super(message, cause);
    }
}
