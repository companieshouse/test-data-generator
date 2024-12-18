package uk.gov.companieshouse.api.testdata.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WebConfigTest {
    private static final String USERS_ENDPOINTS = "/test-data/users**";

    @Mock
    private InterceptorRegistry registry;

    @InjectMocks
    private WebConfig webConfig;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Test
    void shouldAddInternalUserInterceptorToRegistry() {
        when(registry.addInterceptor(any(InternalUserInterceptor.class))).thenReturn(interceptorRegistration);

        webConfig.addInterceptors(registry);

        ArgumentCaptor<InternalUserInterceptor> interceptorCaptor = ArgumentCaptor.forClass(InternalUserInterceptor.class);
        verify(registry, times(1)).addInterceptor(interceptorCaptor.capture());
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/users**");

        InternalUserInterceptor capturedInterceptor = interceptorCaptor.getValue();
        assertNotNull(capturedInterceptor);
    }

    @Test
    void shouldCreateInternalUserInterceptorBean() {
        InternalUserInterceptor interceptor = webConfig.internalUserInterceptor();
        assertNotNull(interceptor);
    }
}