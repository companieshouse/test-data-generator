package uk.gov.companieshouse.api.testdata.interceptors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.interceptor.InternalUserInterceptor;
import uk.gov.companieshouse.api.testdata.Application;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
public class UsersInterceptor extends InternalUserInterceptor {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
       LOG.info("User Interceptor - preHandle");
       if ( super.preHandle( request, response, handler ) ) {
           LOG.info("User Interceptor - preHandle - super returned true");
            final var privileges =
                    Optional.ofNullable( request.getHeader("ERIC-Authorised-Key-Privileges") )
                            .map(s -> s.split(","))
                            .orElse(new String[]{});

            for (String privilege : privileges) {
                LOG.info("User Interceptor - preHandle - privilege: " + privilege);
            }
            final var hasInternalPrivilege = ArrayUtils.contains(privileges, "internal-app");
            LOG.info("User Interceptor - preHandle - hasInternalPrivilege: " + hasInternalPrivilege);

            if( hasInternalPrivilege ){
                LOG.debug( "Caller authorised with internal-app privileges" );
                return true;
            } else {
                LOG.error( "Caller does not have required privileges" );
                response.setStatus( 401 );
                return false;
            }
        }
        return false;
    }
}
