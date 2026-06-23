package uk.gov.companieshouse.api.testdata.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.PublicCompanyRequest;
import uk.gov.companieshouse.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PublicCompanyRequestV1ArgumentResolverTest {

    private final PublicCompanyRequestV1ArgumentResolver resolver = new PublicCompanyRequestV1ArgumentResolver();

    @Test
    void supportsV1RequestBodyParameter() throws Exception {
        Method method = StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    void doesNotSupportNonV1Parameter() throws Exception {
        Method method = StubController.class.getDeclaredMethod("other", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    void returnsNullForBlankRequestBody() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody("   ");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class), 0);

        Object resolved = resolver.resolveArgument(parameter, null, webRequest, null);

        assertNull(resolved);
    }

    @Test
    void parsesValidV1Request() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody("{\"company_status\":\"active\"}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class), 0);

        Object resolved = resolver.resolveArgument(parameter, null, webRequest, null);

        assertNotNull(resolved);
    }

    @Test
    void ignoresUnknownFieldsIncludingAlphabeticalSearch() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody(
                "{\"company_status\":\"active\",\"alphabetical_search\":true}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class), 0);

        Object resolved = resolver.resolveArgument(parameter, null, webRequest, null);

        assertNotNull(resolved);
        PublicCompanyRequest request = (PublicCompanyRequest) resolved;
        assertEquals("active", request.getCompanyStatus());
    }

    @Test
    void logsUnknownFieldsWhenPresent() throws Exception {
        Logger logger = mock(Logger.class);
        PublicCompanyRequestV1ArgumentResolver loggingResolver = new PublicCompanyRequestV1ArgumentResolver(logger);
        NativeWebRequest webRequest = webRequestWithBody(
                "{\"company_status\":\"active\",\"alphabetical_search\":true}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class), 0);

        loggingResolver.resolveArgument(parameter, null, webRequest, null);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<java.util.Map<String, Object>> dataCaptor = ArgumentCaptor.forClass(java.util.Map.class);
        verify(logger).info(eq("Public v1 payload contained unknown fields that were ignored"),
                dataCaptor.capture());

        Map<String, Object> data = dataCaptor.getValue();
        assertEquals("/test-data/company", data.get("path"));
        assertEquals("2026-12-31", data.get("deprecation_enforced_after"));
        @SuppressWarnings("unchecked")
        Set<String> unknownFields = (Set<String>) data.get("unknown_fields");
        assertTrue(unknownFields.contains("alphabetical_search"));
    }

    @Test
    void doesNotLogUnknownFieldsWhenPayloadContainsOnlyPublicFields() throws Exception {
        Logger logger = mock(Logger.class);
        PublicCompanyRequestV1ArgumentResolver loggingResolver = new PublicCompanyRequestV1ArgumentResolver(logger);
        NativeWebRequest webRequest = webRequestWithBody("{\"company_status\":\"active\"}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v1", PublicCompanyRequest.class), 0);

        loggingResolver.resolveArgument(parameter, null, webRequest, null);

        verify(logger, never()).info(eq("Public v1 payload contained unknown fields that were ignored"),
                org.mockito.ArgumentMatchers.anyMap());
    }

    private NativeWebRequest webRequestWithBody(String body) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getInputStream()).thenReturn(
                new ByteArrayServletInputStream(body.getBytes(StandardCharsets.UTF_8)));
        when(servletRequest.getRequestURI()).thenReturn("/test-data/company");

        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(servletRequest);
        return webRequest;
    }

    // Minimal adapter for request-body simulation in resolver unit tests.
    private static final class ByteArrayServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream delegate;

        private ByteArrayServletInputStream(byte[] content) {
            this.delegate = new ByteArrayInputStream(content);
        }

        @Override
        public int read() {
            return delegate.read();
        }

        @Override
        public boolean isFinished() {
            return delegate.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            // Async request body reading is not used in these tests.
        }
    }

    @SuppressWarnings("unused")
    private static class StubController {
        void v1(@RequestBody PublicCompanyRequest request) {
            // Intentionally empty: test inspects this signature via reflection only.
        }

        void other(@RequestBody String request) {
            // Intentionally empty: test inspects this signature via reflection only.
        }
    }
}
