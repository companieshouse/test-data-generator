package uk.gov.companieshouse.api.testdata.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NonNull;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequestV2;

/**
 * Parses InternalCompanyRequestV2 with strict unknown-field validation without changing v1 behavior.
 */
public class InternalCompanyRequestV2ArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper strictV2ObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return InternalCompanyRequestV2.class.equals(parameter.getParameterType())
                && parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        PublicCompanyRequestResolverSupport.RequestPayload requestPayload =
                extractRequestPayload(webRequest);
        if (requestPayload == null) {
            return null;
        }

        HttpServletRequest servletRequest = requestPayload.servletRequest();

        try {
            InternalCompanyRequestV2 resolved =
                    strictV2ObjectMapper.readValue(requestPayload.body(), InternalCompanyRequestV2.class);
            var violations = validator.validate(resolved);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            return resolved;
        } catch (JsonProcessingException | IllegalArgumentException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(servletRequest));
        }
    }

    private PublicCompanyRequestResolverSupport.RequestPayload extractRequestPayload(NativeWebRequest webRequest) {
        try {
            return PublicCompanyRequestResolverSupport.extractRequestPayload(webRequest);
        } catch (PublicCompanyRequestResolverSupport.RequestPayloadReadException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(ex.servletRequest()));
        }
    }
}
