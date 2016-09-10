package com.testservice.filter;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.testservice.domain.User;
import com.testservice.service.TokenService;

/**
 * Checks Authorization header or Authorization query parameter to authorize user. Uses Basic access authentication to
 * authenticate users.
 * 
 * @author taras
 *
 */
@Provider
@PreMatching
@Component
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationRequestFilter implements ContainerRequestFilter {

    private static final String TOKEN_NAME = "Authorization";
    private static final String AUTHENTICATION_SCHEME = "Basic";
    private static final Logger LOGGER = Logger.getLogger(AuthorizationRequestFilter.class);

    @Autowired
    private TokenService tokenService;

    @PostConstruct
    private void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    /**
     * Checks Authorization header or Authorization query parameter to find authentication token, search the principal
     * with the token in cache or ties to load principal from database and sets it in {@link SecurityContext}.
     * 
     * @see javax.ws.rs.container.ContainerRequestFilter#filter(javax.ws.rs.container.ContainerRequestContext)
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String token = requestContext.getHeaderString(TOKEN_NAME);
        LOGGER.info("headed token: " + token);
        if (token == null) {
            Map<String, List<String>> map = requestContext.getUriInfo().getQueryParameters();
            if (!CollectionUtils.isEmpty(map)) {
                token = map.get(TOKEN_NAME).get(0);
                LOGGER.info("query param token: " + token);
            }
        }
        if (token == null) {
            LOGGER.info("token not found");
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                             .entity("User cannot access the resource.")
                                             .build());
        } else {
            token = token.replaceFirst(AUTHENTICATION_SCHEME + " ", "");
            if (!tokenService.contains(token) && !tokenService.tryAuthenticate(token)) {
                LOGGER.info("user not authenticated");
                requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                                                 .entity("User cannot access the resource.")
                                                 .build());
            }
            User user = tokenService.get(token);
            LOGGER.info("user: " + user);
            requestContext.setSecurityContext(new SecurityContext() {

                @Override
                public Principal getUserPrincipal() {
                    return user;
                }

                @Override
                public boolean isUserInRole(String role) {
                    return user.getRole().equals(role);
                }

                @Override
                public boolean isSecure() {
                    return false;
                }

                @Override
                public String getAuthenticationScheme() {
                    return SecurityContext.BASIC_AUTH;
                }

            });
        }
    }
}