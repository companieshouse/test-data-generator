package uk.gov.companieshouse.api.testdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private static final String USERS_ENDPOINTS = "/test-data/user/**";

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(internalUserInterceptor()).addPathPatterns(USERS_ENDPOINTS);
  }

  @Bean
  public InternalUserInterceptor internalUserInterceptor() {
    return new InternalUserInterceptor();
  }
}
