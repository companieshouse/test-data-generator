package uk.gov.companieshouse.api.testdata.config;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;


@ExtendWith(MockitoExtension.class)
class WebConfigTest {

    @Mock
    private InterceptorRegistry registry;

    @InjectMocks
    private WebConfig webConfig;

    @Mock
    private InterceptorRegistration interceptorRegistration;

    @Test
    void shouldAddInternalUserInterceptorToRegistry() {
        ArgumentCaptor<InternalUserInterceptor> interceptorCaptor = ArgumentCaptor.forClass(InternalUserInterceptor.class);
        when(registry.addInterceptor(interceptorCaptor.capture())).thenReturn(interceptorRegistration);

        webConfig.addInterceptors(registry);

        verify(registry, times(1)).addInterceptor(any(InternalUserInterceptor.class));
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/user/**");

        assertNotNull(interceptorCaptor.getValue());
    }

    @Test
    void shouldCreateInternalUserInterceptorBean() {
        InternalUserInterceptor interceptor = webConfig.internalUserInterceptor();
        assertNotNull(interceptor);
    }
}