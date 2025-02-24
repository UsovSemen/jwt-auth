package org.practice.jwtauth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.practice.jwtauth.util.JwtUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthorizationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @InjectMocks
    private JwtAuthorizationFilter jwtAuthorizationFilter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void testDoFilter_ValidToken() throws Exception {
        // init
        String token = "test.valid.token";
        String authorizationHeader = "Bearer " + token;
        Jws<Claims> claimsJws = mock(Jws.class);
        Claims claims = mock(Claims.class);

        when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        when(jwtUtil.isValid(token)).thenReturn(true);
        when(jwtUtil.parseToken(token)).thenReturn(claimsJws);
        when(claimsJws.getPayload()).thenReturn(claims);
        when(claims.getSubject()).thenReturn("John");

        jwtAuthorizationFilter.doFilter(request, response, chain);

        //checking
        verify(chain, atLeast(1)).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertTrue(authentication.isAuthenticated());
        assertEquals("John", authentication.getName());
    }

    @Test
    public void testDoFilter_NoHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtAuthorizationFilter.doFilter(request, response, chain);

        verify(chain, atLeast(1)).doFilter(request, response);
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
    }

    @Test
    void testDoFilter_AuthHeaderPrefixInvalid() throws Exception {

        String invalidAuthorizationHeader = "InvalidPrefix invalid.token.here";
        when(request.getHeader("Authorization")).thenReturn(invalidAuthorizationHeader);

        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain, atLeast(1)).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilter_AuthInvalidToken() throws Exception {
        String token = "test.invalid.token";
        String invalidAuthorizationHeader = "Bearer " + token;
        when(request.getHeader("Authorization")).thenReturn(invalidAuthorizationHeader);

        jwtAuthorizationFilter.doFilterInternal(request, response, chain);

        verify(chain, never()).doFilter(request, response);
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
    }
}