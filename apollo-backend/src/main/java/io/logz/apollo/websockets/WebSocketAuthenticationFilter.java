package io.logz.apollo.websockets;

import io.logz.apollo.auth.PermissionsValidator;
import io.logz.apollo.auth.TokenConverter;
import io.logz.apollo.common.HttpStatus;
import io.logz.apollo.common.QueryStringParser;
import io.logz.apollo.dao.DeploymentPermissionDao;
import io.logz.apollo.database.ApolloMyBatis;
import io.logz.apollo.database.ApolloMyBatis.ApolloMyBatisSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by roiravhon on 5/23/17.
 */
public class WebSocketAuthenticationFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketAuthenticationFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        Optional<String> token = Stream.of(((HttpServletRequest) servletRequest).getCookies())
                .filter(cookie -> cookie.getName().equals("_token"))
                .findFirst()
                .map(Cookie::getValue);

        if (token.isPresent()) {
            try {
                String userName = TokenConverter.convertTokenToUser(token.get());
                int environmentId = QueryStringParser.getIntFromQueryString(((HttpServletRequest) servletRequest).getQueryString(), ContainerExecEndpoint.QUERY_STRING_ENVIRONMENT_KEY);
                int serviceId = QueryStringParser.getIntFromQueryString(((HttpServletRequest) servletRequest).getQueryString(), ContainerExecEndpoint.QUERY_STRING_SERVICE_KEY);

                try (ApolloMyBatisSession apolloMyBatisSession = ApolloMyBatis.getSession()) {
                    DeploymentPermissionDao deploymentPermissionDao = apolloMyBatisSession.getDao(DeploymentPermissionDao.class);
                    if (PermissionsValidator.isAllowedToDeploy(serviceId, environmentId, deploymentPermissionDao.getPermissionsByUser(userName))) {
                        logger.info("Granted Live-Session permission to user {} on service {} and environment {}", userName, serviceId, environmentId);
                        filterChain.doFilter(servletRequest, servletResponse);
                    } else {
                        logger.info("User {} have no permissions to exec to service {} on environment {}", userName, serviceId, environmentId);
                        ((HttpServletResponse) servletResponse).setStatus(HttpStatus.FORBIDDEN);
                    }
                }
            } catch (Exception e) {
                logger.warn("Got exception while validating user permissions for deployment, assuming no!", e);
                ((HttpServletResponse) servletResponse).setStatus(HttpStatus.FORBIDDEN);
            }
        } else {
            ((HttpServletResponse) servletResponse).setStatus(HttpStatus.UNAUTHORIZED);
        }
    }

    @Override
    public void destroy() {

    }
}
