package uk.gov.companieshouse.api.testdata.exception;

public class InvalidAuthCodeException extends Exception {

    private static final long serialVersionUID = -1089294951310096902L;

    private final String companyNumber;

    public InvalidAuthCodeException(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

}
