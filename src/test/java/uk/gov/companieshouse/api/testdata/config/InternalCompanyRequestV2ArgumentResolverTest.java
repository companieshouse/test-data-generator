package uk.gov.companieshouse.api.testdata.config;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.NativeWebRequest;
import uk.gov.companieshouse.api.testdata.model.rest.request.InternalCompanyRequestV2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class InternalCompanyRequestV2ArgumentResolverTest {

    private final InternalCompanyRequestV2ArgumentResolver resolver = new InternalCompanyRequestV2ArgumentResolver();

    @Test
    void supportsV2RequestBodyParameter() throws Exception {
        Method method = StubController.class.getDeclaredMethod("v2", InternalCompanyRequestV2.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    void doesNotSupportNonV2Parameter() throws Exception {
        Method method = StubController.class.getDeclaredMethod("v1", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    void returnsNullForBlankRequestBody() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody("   ");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v2", InternalCompanyRequestV2.class), 0);

        Object resolved = resolver.resolveArgument(parameter, null, webRequest, null);

        assertNull(resolved);
    }

    @Test
    void parsesValidV2Request() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody("{\"company_status\":\"active\"}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v2", InternalCompanyRequestV2.class), 0);

        Object resolved = resolver.resolveArgument(parameter, null, webRequest, null);

        assertNotNull(resolved);
    }

    @Test
    void rejectsUnknownFields() throws Exception {
        NativeWebRequest webRequest = webRequestWithBody("{\"unknown_field\":\"x\"}");
        MethodParameter parameter = new MethodParameter(
                StubController.class.getDeclaredMethod("v2", InternalCompanyRequestV2.class), 0);

        assertThrows(HttpMessageNotReadableException.class,
                () -> resolver.resolveArgument(parameter, null, webRequest, null));
    }

    private NativeWebRequest webRequestWithBody(String body) throws IOException {
        HttpServletRequest servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getInputStream()).thenReturn(
                new ByteArrayServletInputStream(body.getBytes(StandardCharsets.UTF_8)));

        NativeWebRequest webRequest = mock(NativeWebRequest.class);
        when(webRequest.getNativeRequest(HttpServletRequest.class)).thenReturn(servletRequest);
        return webRequest;
    }

    private static final class ByteArrayServletInputStream extends ServletInputStream {
        private final InputStream delegate;

        private ByteArrayServletInputStream(byte[] content) {
            this.delegate = new ByteArrayInputStream(content);
        }

        @Override
        public int read() throws IOException {
            return delegate.read();
        }

        @Override
        public boolean isFinished() {
            try {
                return delegate.available() == 0;
            } catch (IOException ex) {
                return true;
            }
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

    private static class StubController {
        void v2(@RequestBody InternalCompanyRequestV2 request) {
        }

        void v1(@RequestBody String request) {
        }
    }
}
