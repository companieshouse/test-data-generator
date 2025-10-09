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
    private static final String USER_COMPANY_ASSOCIATION_ENDPOINTS =
            "/test-data/associations/**";
    private static final String TRANSACTIONS_ENDPOINTS = "/test-data/transactions/**";
    private static final String CERTIFICATES_ENDPOINTS = "/test-data/certificates/**";
    private static final String CERTIFIED_COPIES_ENDPOINTS = "/test-data/certified-copies/**";
    private static final String MISSING_IMAGE_DELIVERIES = "/test-data/missing-image-deliveries/**";
    private static final String SIC_CODE_KEYWORD = "/test-data/combined-sic-activities/**";
    private static final String ADMIN_PERMISSIONS_ENDPOINTS = "/test-data/admin-permissions/**";

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(internalUserInterceptor())
                .addPathPatterns(USERS_ENDPOINTS)
                .addPathPatterns(IDENTITY_ENDPOINTS)
                .addPathPatterns(ACSP_ENDPOINTS)
                .addPathPatterns(APPEALS_ENDPOINTS)
                .addPathPatterns(ACCOUNT_PENALTIES_ENDPOINTS)
                .addPathPatterns(USER_COMPANY_ASSOCIATION_ENDPOINTS)
                .addPathPatterns(TRANSACTIONS_ENDPOINTS)
                .addPathPatterns(CERTIFICATES_ENDPOINTS)
                .addPathPatterns(CERTIFIED_COPIES_ENDPOINTS)
                .addPathPatterns(MISSING_IMAGE_DELIVERIES)
                .addPathPatterns(SIC_CODE_KEYWORD)
                .addPathPatterns(ADMIN_PERMISSIONS_ENDPOINTS);
    }

    @Bean
    public InternalUserInterceptor internalUserInterceptor() {
        return new InternalUserInterceptor();
    }
}
