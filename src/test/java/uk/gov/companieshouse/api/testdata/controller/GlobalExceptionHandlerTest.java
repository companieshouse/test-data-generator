package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;

import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationError;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationErrors;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleHttpMessageNotReadableNullCause() throws Exception {
        Throwable cause = null;
        HttpInputMessage httpInputMessage = null;
        Exception ex = new HttpMessageNotReadableException("ex", cause, httpInputMessage);
        WebRequest request = Mockito.mock(WebRequest.class);

        final ValidationError expectedError = new ValidationError("invalid request", null, null, "ch:validation");

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);
        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(expectedError));
    }
    
    @Test
    void handleHttpMessageNotReadableUnhandledCause() throws Exception {
        Throwable cause = new Throwable("throwable message");
        HttpInputMessage httpInputMessage = null;
        Exception ex = new HttpMessageNotReadableException("ex", cause, httpInputMessage);
        WebRequest request = Mockito.mock(WebRequest.class);

        final ValidationError expectedError = new ValidationError("invalid request", null, null, "ch:validation");

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);
        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(expectedError));
    }

    @Test
    void handleHttpMessageNotReadableCompanySpecInvalidFormatException() throws Exception {
        InvalidFormatException cause = Mockito.mock(InvalidFormatException.class);
        HttpInputMessage httpInputMessage = null;
        Exception ex = new HttpMessageNotReadableException("ex", cause, httpInputMessage);
        WebRequest request = Mockito.mock(WebRequest.class);

        when(cause.getPathReference())
                .thenReturn("uk.gov.companieshouse.api.testdata.model.rest.request.CompanyRequest[\"jurisdiction\"]");

        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement("CompanyRequest", "getJurisdiction", "CompanyRequest.java", 123)
        };
        when(cause.getStackTrace()).thenReturn(stackTrace);

        final ValidationError expectedError = new ValidationError("invalid jurisdiction", null, null, "ch:validation");

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);
        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(expectedError));
    }

    @Test
    void handleHttpMessageNotReadableOtherInvalidFormatException() throws Exception {
        InvalidFormatException cause = Mockito.mock(InvalidFormatException.class);
        HttpInputMessage httpInputMessage = null;
        Exception ex = new HttpMessageNotReadableException("ex", cause, httpInputMessage);
        WebRequest request = Mockito.mock(WebRequest.class);

        when(cause.getPathReference()).thenReturn("unrecognised path reference");

        StackTraceElement[] stackTrace = new StackTraceElement[] {
            new StackTraceElement("CompanyRequest", "getJurisdiction", "CompanyRequest.java", 123)
        };
        when(cause.getStackTrace()).thenReturn(stackTrace);

        final ValidationError expectedError = new ValidationError("invalid request", null, null, "ch:validation");

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);
        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(expectedError));
    }

    @Test
    void handleMethodArgumentNotValid() throws Exception {
        List<FieldError> fieldErrors = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            fieldErrors.add(new FieldError("name " + i, "field" + i, "field error message" + i));
        }

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        WebRequest request = Mockito.mock(WebRequest.class);

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);
        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(fieldErrors.size(), errors.getErrorCount());
        for (FieldError e : fieldErrors) {
            final ValidationError expectedError = new ValidationError(e.getDefaultMessage(), null, null, "ch:validation");
            assertTrue(errors.containsError(expectedError));
        }
    }

    @Test
    void handleNoDataFoundException() {
        final InvalidAuthCodeException ex = new InvalidAuthCodeException("1234");

        ResponseEntity<ValidationErrors> response = handler.handleInvalidAuthCode(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(1, response.getBody().getErrorCount());

        final ValidationError expectedError = new ValidationError("incorrect company auth_code", null, null, "ch:validation");
        assertTrue(response.getBody().containsError(expectedError));
    }
}
