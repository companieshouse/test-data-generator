package uk.gov.companieshouse.api.testdata.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.core.JacksonException.Reference;

import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequest;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationError;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationErrors;


@ExtendWith(MockitoExtension.class)
class ApiExceptionHandlerTest {

    private ApiExceptionHandler handler = new ApiExceptionHandler();

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
    void handleHttpMessageNotReadableReturnsGenericInvalidRequest() throws Exception {
        InvalidFormatException cause = new InvalidFormatException(
                null,
                "error",
                "input",
                String.class
        );
        HttpInputMessage httpInputMessage = null;
        Exception ex = new HttpMessageNotReadableException("ex", cause, httpInputMessage);
        WebRequest request = Mockito.mock(WebRequest.class);

        final ValidationError expectedError =
                new ValidationError("invalid request", null, null, "ch:validation");

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ValidationErrors);

        ValidationErrors errors = (ValidationErrors) response.getBody();
        assertEquals(1, errors.getErrorCount());
        assertTrue(errors.containsError(expectedError));
    }

    @Test
    void handleHttpMessageNotReadableInvalidFormatExceptionWithFieldName() throws Exception {
        InvalidFormatException cause = new InvalidFormatException(
                null,
                "error",
                "invalid-value",
                String.class
        );
        Reference ref = new Reference(InternalCompanyRequest.class, "jurisdiction");

        cause.prependPath(ref);

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("ex", cause, null);

        WebRequest request = Mockito.mock(WebRequest.class);

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertNotNull(response.getBody());
        ValidationErrors errors = (ValidationErrors) response.getBody();
        ValidationError actual = errors.getErrors().iterator().next();

        assertEquals("invalid jurisdiction", actual.getError());
    }

    @Test
    void extractFieldNameNoPropertyNameReturnsInvalidRequest() throws Exception {

        InvalidFormatException cause =
                new InvalidFormatException(null, "msg", "value", String.class);

        Reference ref = Mockito.mock(Reference.class);
        when(ref.getPropertyName()).thenReturn(null);

        cause.prependPath(ref);

        HttpMessageNotReadableException ex =
                new HttpMessageNotReadableException("ex", cause, null);

        WebRequest request = Mockito.mock(WebRequest.class);

        ResponseEntity<Object> response = handler.handleException(ex, request);

        assertNotNull(response.getBody());
        ValidationErrors errors = (ValidationErrors) response.getBody();

        assertEquals("invalid request",
                errors.getErrors().iterator().next().getError());
    }

    @Test
    void handleMethodArgumentNotValid() throws Exception {
        List<FieldError> fieldErrors = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            fieldErrors.add(new FieldError("name " + i, "field" + i, "field error message" + i));
        }

        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        Method method = ApiExceptionHandlerTest.class
                .getDeclaredMethod("dummyMethod", String.class);
        MethodParameter methodParameter = new MethodParameter(method, 0);
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(
                methodParameter, bindingResult);
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

    @Test
    void handleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid officer role for LLP company type: director");

        ResponseEntity<ValidationErrors> response = handler.handleIllegalArgument(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(1, response.getBody().getErrorCount());
        ValidationError expectedError = new ValidationError(
                "Invalid officer role for LLP company type: director", null, null, "ch:validation");
        assertTrue(response.getBody().containsError(expectedError));
    }

    @Test
    void handleDataExceptionRemainsInternalServerError() throws Exception {
        Method method = ApiExceptionHandler.class.getDeclaredMethod("handleDataException", DataException.class);
        ResponseStatus responseStatus = method.getAnnotation(ResponseStatus.class);

        assertNotNull(responseStatus);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseStatus.value());
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String value) {
        // Used only to construct MethodParameter for MethodArgumentNotValidException tests.
    }
}
