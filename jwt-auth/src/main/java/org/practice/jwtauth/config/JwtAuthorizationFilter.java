package org.practice.jwtauth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.practice.jwtauth.util.JwtUtil;
import org.practice.jwtauth.util.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith(Params.BEARER_PREFIX.getValue())) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(Params.BEARER_PREFIX.getValue().length());
        logger.info("JWT token: {}", token);
        if (!jwtUtil.isValid(token)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        Jws<Claims> claimsJws = jwtUtil.parseToken(token);
        String jsonSubject = claimsJws.getPayload().getSubject();
        Authentication authentication = new UsernamePasswordAuthenticationToken(jsonSubject, "", new ArrayList<>());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        logger.info("JWT token jsonSubject: {}", jsonSubject);
        filterChain.doFilter(request, response);
    }

}
