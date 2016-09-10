package com.testservice.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import javax.annotation.PostConstruct;
import javax.annotation.Priority;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Logs all incoming requests.
 * 
 * @author taras
 *
 */
@Provider
@PreMatching
@Component
@Priority(Priorities.AUTHENTICATION)
public class LoggingRequestFilter implements ContainerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(LoggingRequestFilter.class);

    private static final String DELIMITER = "; ";
    private static final String IP_ADDRESS_HEADER = "X-FORWARDED-FOR";

    private @Context HttpServletRequest request;

    @PostConstruct
    private void init() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    /**
    * Logs client ip address, request method, headers and request body of incoming requests.
    * 
    */
    @Override
    public void filter(ContainerRequestContext requestContext) {
        String ipAddress = request.getHeader(IP_ADDRESS_HEADER);
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        StringBuilder out = new StringBuilder();
        out.append("From: ");
        out.append(ipAddress);
        out.append(DELIMITER);
        out.append(requestContext.getMethod());
        out.append(DELIMITER);
        out.append(requestContext.getUriInfo().getAbsolutePath());
        out.append(DELIMITER);
        out.append("Headers: ");
        out.append(requestContext.getHeaders());

        try {
            InputStream is = requestContext.getEntityStream();
            if (is.available() > 0) {
                byte[] bytes = IOUtils.toByteArray(is);
                out.append(DELIMITER);
                out.append("Entity: ");
                out.append(new String(bytes, StandardCharsets.UTF_8));
                requestContext.setEntityStream(new ByteArrayInputStream(bytes));
            }
        } catch (IOException e) {
            LOGGER.error("Unable to get body", e);
        }

        LOGGER.info(out.toString());
    }
}