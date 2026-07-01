package uk.gov.companieshouse.api.testdata.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StreamUtils;
import org.springframework.web.context.request.NativeWebRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

final class CompanyRequestResolverSupport {

    private CompanyRequestResolverSupport() {
    }

    static RequestPayload extractRequestPayload(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (servletRequest == null) {
            return null;
        }

        String body;
        try {
            body = StreamUtils.copyToString(servletRequest.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new RequestPayloadReadException(servletRequest, ex);
        }

        if (body.isBlank()) {
            return null;
        }

        return new RequestPayload(servletRequest, body);
    }

    record RequestPayload(HttpServletRequest servletRequest, String body) {
    }

    static final class RequestPayloadReadException extends RuntimeException {
        private final transient HttpServletRequest servletRequest;

        RequestPayloadReadException(HttpServletRequest servletRequest, IOException cause) {
            super("Failed to read request payload", cause);
            this.servletRequest = servletRequest;
        }

        HttpServletRequest servletRequest() {
            return servletRequest;
        }
    }
}
