package org.practice.jwtauth.config;

import jakarta.servlet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CsrfTokenLogger implements Filter {

    private final Logger LOGGER = LoggerFactory.getLogger(CsrfTokenLogger.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        CsrfToken token =
                (CsrfToken) servletRequest.getAttribute("_csrf");
        LOGGER.info("CSRF token " + token.getToken());
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
