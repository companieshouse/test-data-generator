package uk.gov.companieshouse.api.testdata.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.util.StreamUtils;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequestV2;

import java.nio.charset.StandardCharsets;

/**
 * Parses PublicCompanyRequestV2 with strict unknown-field validation without changing v1 behavior.
 */
public class PublicCompanyRequestV2ArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper strictV2ObjectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PublicCompanyRequestV2.class.equals(parameter.getParameterType())
                && parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            return null;
        }

        String body = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            return strictV2ObjectMapper.readValue(body, PublicCompanyRequestV2.class);
        } catch (Exception ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(servletRequest));
        }
    }
}

