package uk.gov.companieshouse.api.testdata.config;

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
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;

/**
 * Parses PublicCompanyRequestV2 with strict unknown-field validation without changing v1 behavior.
 */
public class PublicCompanyRequestV2ArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper strictV2ObjectMapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PublicCompanyRequestV2.class.equals(parameter.getParameterType())
                && parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        CompanyRequestResolverSupport.RequestPayload requestPayload =
                extractRequestPayload(webRequest);
        if (requestPayload == null) {
            return null;
        }

        HttpServletRequest servletRequest = requestPayload.servletRequest();

        try {
            PublicCompanyRequestV2 resolved =
                    strictV2ObjectMapper.readValue(requestPayload.body(), PublicCompanyRequestV2.class);
            var violations = validator.validate(resolved);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException(violations);
            }
            return resolved;
        } catch (JacksonException | IllegalArgumentException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(servletRequest));
        }
    }

    private CompanyRequestResolverSupport.RequestPayload extractRequestPayload(NativeWebRequest webRequest) {
        try {
            return CompanyRequestResolverSupport.extractRequestPayload(webRequest);
        } catch (CompanyRequestResolverSupport.RequestPayloadReadException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(ex.servletRequest()));
        }
    }
}
