package uk.gov.companieshouse.api.testdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private static final String USERS_ENDPOINTS = "/test-data/user/**";
    private static final String IDENTITY_ENDPOINTS = "/test-data/identity/**";
    private static final String ACSP_ENDPOINTS = "/test-data/acsp-members/**";
    private static final String APPEALS_ENDPOINTS = "/test-data/appeals/**";
    private static final String ACCOUNT_PENALTIES_ENDPOINTS = "/test-data/penalties/**";

    private static final String TRANSACTIONS_ENDPOINTS = "/test-data/transactions/**";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor())
                .addPathPatterns(USERS_ENDPOINTS)
                .addPathPatterns(IDENTITY_ENDPOINTS)
                .addPathPatterns(ACSP_ENDPOINTS)
                .addPathPatterns(APPEALS_ENDPOINTS)
                .addPathPatterns(ACCOUNT_PENALTIES_ENDPOINTS)
                .addPathPatterns(TRANSACTIONS_ENDPOINTS);

    }

    @Bean
    public InternalUserInterceptor internalUserInterceptor() {
        return new InternalUserInterceptor();
    }
}
