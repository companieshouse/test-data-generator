package uk.gov.companieshouse.api.testdata.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.testdata.interceptors.UsersInterceptor;


@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UsersInterceptor usersInterceptor;

    private static final String USERS_ENDPOINTS = "/test-data/users/**";


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
       registry.addInterceptor( usersInterceptor ).addPathPatterns( USERS_ENDPOINTS );
    }
}

