package uk.gov.companieshouse.api.testdata.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Prepends the custom v2 company argument resolvers before Spring's built-in ones.
 * <p>
 * {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer#addArgumentResolvers}
 * only appends resolvers, so Spring's {@code RequestResponseBodyMethodProcessor} (which handles
 * {@code @RequestBody}) always wins first. By prepending here, via
 * {@link SmartInitializingSingleton} (which runs after all singletons are constructed), we ensure
 * the strict V2 resolvers take priority.
 * </p>
 */
@Component
public class ArgumentResolverConfigurer implements SmartInitializingSingleton {

    private final RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    public ArgumentResolverConfigurer(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        this.requestMappingHandlerAdapter = requestMappingHandlerAdapter;
    }

    @Override
    public void afterSingletonsInstantiated() {
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
        resolvers.add(new PublicCompanyRequestV2ArgumentResolver());
        resolvers.add(new InternalCompanyRequestV2ArgumentResolver());
        resolvers.addAll(Objects.requireNonNull(requestMappingHandlerAdapter.getArgumentResolvers()));
        requestMappingHandlerAdapter.setArgumentResolvers(resolvers);
    }
}
