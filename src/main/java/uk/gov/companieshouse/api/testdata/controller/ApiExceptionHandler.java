package uk.gov.companieshouse.api.testdata.controller;

import java.util.List;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.jspecify.annotations.NonNull;
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
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.UnrecognizedPropertyException;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.exception.DataException;
import uk.gov.companieshouse.api.testdata.exception.InvalidAuthCodeException;
import uk.gov.companieshouse.api.testdata.exception.NoDataFoundException;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationError;
import uk.gov.companieshouse.api.testdata.model.rest.validation.ValidationErrors;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private static final String INVALID_REQUEST = "invalid request";
    public static final String INVALID = "invalid ";
    private static final String API_VERSION_HEADER = "X-API-Version";

    @ExceptionHandler(value = {ConstraintViolationException.class})
    protected ResponseEntity<ValidationErrors> handleConstraintViolation(ConstraintViolationException ex) {
        logException(ex);
        ValidationErrors errors = new ValidationErrors();
        ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .map(this::createValidationError)
                .forEach(errors::addError);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {DataException.class})
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    protected void handleDataException(DataException ex) {
        logException(ex);
    }

    @ExceptionHandler(value = {NoDataFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    protected void handleNoDataFoundException(NoDataFoundException ex) {
        logException(ex);
    }

    @ExceptionHandler(value = {InvalidAuthCodeException.class})
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ValidationErrors> handleInvalidAuthCode(InvalidAuthCodeException ex) {
        LOG.error("Incorrect company auth_code provided for company " + ex.getCompanyNumber());

        ValidationErrors errors = new ValidationErrors();
        errors.addError(createValidationError("incorrect company auth_code"));
        return new ResponseEntity<>(errors, HttpStatus.UNAUTHORIZED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            @NonNull HttpMessageNotReadableException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        logException(ex);

        String message = resolveMessage(ex, request);

        ValidationErrors errors = new ValidationErrors();
        errors.addError(createValidationError(message));

        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    private String resolveMessage(HttpMessageNotReadableException ex, WebRequest request) {
        boolean isV2Request = isV2Request(request);
        var upe = findCause(ex, UnrecognizedPropertyException.class);
        if (upe != null) {
            return INVALID + upe.getPropertyName();
        }
        var ife = findCause(ex, InvalidFormatException.class);
        if (ife != null) {
            return extractFieldMessage(ife.getPath(), isV2Request);
        }
        // Handle mapping exceptions that still carry path information even when they are not InvalidFormatException.
        var jme = findCause(ex, DatabindException.class);
        if (jme != null) {
            return extractFieldMessage(jme.getPath(), isV2Request);
        }

        return INVALID_REQUEST;
    }

    private <T extends Throwable> T findCause(Throwable throwable, Class<T> type) {
        Throwable current = throwable;
        while (current != null) {
            if (type.isInstance(current)) {
                return type.cast(current);
            }
            current = current.getCause();
        }
        return null;
    }

    private String extractFieldMessage(List<JacksonException.Reference> path, boolean isV2Request) {
        if (path == null || path.isEmpty()) {
            return INVALID_REQUEST;
        }

        // TODO: Remove this block when v1 is retired - v2 returns specific field names for collection errors.
        // Preserve legacy v1 behavior for collection element coercion errors.
        // If Jackson path includes an array index, return generic invalid request.
        if (!isV2Request) {
            for (JacksonException.Reference ref : path) {
                if (ref.getIndex() >= 0) {
                    return INVALID_REQUEST;
                }
            }
        }

        for (JacksonException.Reference ref : path) {

            String fieldName = extractFieldName(ref.getDescription());

            if (fieldName != null) {
                return INVALID + fieldName;
            }
        }

        return INVALID_REQUEST;
    }

    private String extractFieldName(String desc) {
        if (desc == null || !desc.contains("[\"")) {
            return null;
        }

        int start = desc.indexOf("[\"") + 2;
        int end = desc.indexOf("\"]");

        if (start > 1 && end > start) {
            return desc.substring(start, end);
        }

        return null;
    }

    private boolean isV2Request(WebRequest request) {
        if (!(request instanceof ServletWebRequest servletWebRequest)) {
            return false;
        }
        String versionHeader = servletWebRequest.getRequest().getHeader(API_VERSION_HEADER);
        if ("2".equals(versionHeader)) {
            return true;
        }
        String uri = servletWebRequest.getRequest().getRequestURI();
        return uri != null && uri.contains("/v2/");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request) {
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
