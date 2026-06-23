package uk.gov.companieshouse.api.testdata.config;

import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Parses PublicCompanyRequest while keeping v1 permissive and logging unknown fields.
 */
@NullMarked
public class PublicCompanyRequestV1ArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String V1_UNKNOWN_FIELDS_DEPRECATION_DATE = "2026-12-31";

    private final Logger logger;
    private final ObjectMapper permissiveObjectMapper;
    private final Set<String> knownPublicFields;

    public PublicCompanyRequestV1ArgumentResolver() {
        this(LoggerFactory.getLogger(Application.APPLICATION_NAME));
    }

    PublicCompanyRequestV1ArgumentResolver(Logger logger) {
        this.logger = logger;
        this.permissiveObjectMapper = new ObjectMapper()
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.knownPublicFields = loadKnownPublicFields();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PublicCompanyRequest.class.equals(parameter.getParameterType())
                && parameter.hasParameterAnnotation(RequestBody.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  @Nullable ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  @Nullable WebDataBinderFactory binderFactory) {
        PublicCompanyRequestResolverSupport.RequestPayload requestPayload =
                extractRequestPayload(webRequest);
        if (requestPayload == null) {
            return null;
        }

        HttpServletRequest servletRequest = requestPayload.servletRequest();

        try {
            JsonNode payload = permissiveObjectMapper.readTree(requestPayload.body());
            logUnknownFieldsIfPresent(payload, servletRequest);
            return permissiveObjectMapper.treeToValue(payload, PublicCompanyRequest.class);
        } catch (JsonProcessingException | IllegalArgumentException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(servletRequest));
        }
    }

    private PublicCompanyRequestResolverSupport.@Nullable RequestPayload extractRequestPayload(NativeWebRequest webRequest) {
        try {
            return PublicCompanyRequestResolverSupport.extractRequestPayload(webRequest);
        } catch (PublicCompanyRequestResolverSupport.RequestPayloadReadException ex) {
            throw new HttpMessageNotReadableException(
                    "invalid request",
                    ex,
                    new ServletServerHttpRequest(ex.servletRequest()));
        }
    }

    private Set<String> loadKnownPublicFields() {
        JavaType javaType = permissiveObjectMapper.getTypeFactory().constructType(PublicCompanyRequest.class);
        BeanDescription beanDescription = permissiveObjectMapper.getDeserializationConfig().introspect(javaType);
        Set<String> fieldNames = new HashSet<>();
        beanDescription.findProperties().forEach(property -> fieldNames.add(property.getName()));
        return fieldNames;
    }

    private void logUnknownFieldsIfPresent(JsonNode payload, HttpServletRequest request) {
        if (!payload.isObject()) {
            return;
        }

        Set<String> unknownFields = new HashSet<>();
        Iterator<String> fieldNames = payload.fieldNames();
        while (fieldNames.hasNext()) {
            String fieldName = fieldNames.next();
            if (!knownPublicFields.contains(fieldName)) {
                unknownFields.add(fieldName);
            }
        }

        if (!unknownFields.isEmpty()) {
            Map<String, Object> data = new HashMap<>();
            data.put("path", request.getRequestURI());
            data.put("unknown_fields", unknownFields);
            data.put("deprecation_enforced_after", V1_UNKNOWN_FIELDS_DEPRECATION_DATE);
            logger.info("Public v1 payload contained unknown fields that were ignored", data);
        }
    }
}
