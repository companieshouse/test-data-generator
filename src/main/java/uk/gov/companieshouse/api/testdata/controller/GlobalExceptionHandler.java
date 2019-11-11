package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { DataException.class })
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected void handleDataException(DataException ex) {
        logException(ex);
    }
    
    @ExceptionHandler(value = { NoDataFoundException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected void handleNoDataFoundException(NoDataFoundException ex) {
        logException(ex);
    }

    private void logException(Exception ex) {
        // TODO Log exception
    }

}
