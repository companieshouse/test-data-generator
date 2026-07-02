package uk.gov.companieshouse.api.testdata.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArgumentResolverConfigurerTest {

    @Mock
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;

    private ArgumentResolverConfigurer argumentResolverConfigurer;

    @BeforeEach
    void setUp() {
        argumentResolverConfigurer = new ArgumentResolverConfigurer(requestMappingHandlerAdapter);
    }

    @Test
    void shouldPrependV2ResolversBeforeExistingOnes() {
        HandlerMethodArgumentResolver existingResolver = mock(HandlerMethodArgumentResolver.class);
        when(requestMappingHandlerAdapter.getArgumentResolvers()).thenReturn(List.of(existingResolver));

        argumentResolverConfigurer.afterSingletonsInstantiated();

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<HandlerMethodArgumentResolver>> captor = ArgumentCaptor.forClass(List.class);
        verify(requestMappingHandlerAdapter).setArgumentResolvers(captor.capture());

        List<HandlerMethodArgumentResolver> result = captor.getValue();
        assertEquals(3, result.size());
        assertInstanceOf(PublicCompanyRequestV2ArgumentResolver.class, result.get(0));
        assertInstanceOf(InternalCompanyRequestV2ArgumentResolver.class, result.get(1));
        assertEquals(existingResolver, result.get(2));
    }
}
