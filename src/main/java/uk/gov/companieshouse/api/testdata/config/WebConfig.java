package uk.gov.companieshouse.api.testdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String TEST_DATA_INTERNAL_ENDPOINTS = "/test-data/internal/**";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor())
                .addPathPatterns(TEST_DATA_INTERNAL_ENDPOINTS);
    }

    @Bean
    public InternalUserInterceptor internalUserInterceptor() {
        return new InternalUserInterceptor();
    }
}
