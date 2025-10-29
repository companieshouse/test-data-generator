package uk.gov.companieshouse.api.testdata.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        when(registry.addInterceptor(any(InternalUserInterceptor.class)))
                .thenReturn(interceptorRegistration);
        when(interceptorRegistration.addPathPatterns(any(String[].class)))
                .thenReturn(interceptorRegistration);
        webConfig.addInterceptors(registry);

        verify(registry, times(1)).addInterceptor(any(InternalUserInterceptor.class));
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/user/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/identity/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/acsp-members/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/appeals/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/penalties/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/associations/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/transactions/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/certificates/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/certified-copies/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/missing-image-deliveries/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/combined-sic-activities/**");
        verify(interceptorRegistration, times(1)).addPathPatterns("/test-data/admin-permissions/**");
    }

    @Test
    void shouldCreateInternalUserInterceptorBean() {
        InternalUserInterceptor interceptor = webConfig.internalUserInterceptor();
        assertNotNull(interceptor);
    }
}
