package uk.gov.companieshouse.api.testdata.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.CompanySpec;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationError;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationErrors;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

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

    @ExceptionHandler(value = { InvalidAuthCodeException.class })
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    protected ResponseEntity<ValidationErrors> handleInvalidAuthCode(InvalidAuthCodeException ex) {
        LOG.error("Incorrect company auth_code provided for company " + ex.getCompanyNumber());

        ValidationErrors errors = new ValidationErrors();
        errors.addError(createValidationError("incorrect company auth_code"));
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status,
            WebRequest request) {
        logException(ex);

        String message = "invalid request";
        Throwable cause = ex.getCause();
        if (cause instanceof InvalidFormatException ife) {
            String pathReference = ife.getPathReference();
            if (pathReference != null && pathReference.startsWith(CompanySpec.class.getName())) {
                // Handle invalid format in CompanySpec (failed to deserialize enum)
                String invalidField = pathReference.substring(pathReference.indexOf("[\"") + 2,
                        pathReference.indexOf("\"]"));
                message = "invalid " + invalidField;
            }
        }

        ValidationErrors errors = new ValidationErrors();
        errors.addError(createValidationError(message));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        logException(ex);

        ValidationErrors errors = new ValidationErrors();
        ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
                .map(this::createValidationError)
                .forEach(errors::addError);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private ValidationError createValidationError(String message) {
        return new ValidationError(message, null, null, "ch:validation");
    }

    private void logException(Exception ex) {
        LOG.error(ex);
    }
}