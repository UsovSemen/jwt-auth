package org.practice.jwtauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.SignatureException;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    JwtUtil jwtUtil;

    @BeforeEach
    public void setUp() {
        jwtUtil = new JwtUtil();
    }

    @Test
    public void testGenerateAccessToken() {

        String name = "John Doe";
        String accessToken = jwtUtil.generateAccessToken(name);

        assertNotNull(accessToken);
        assertFalse(accessToken.isEmpty());

        Jws<Claims> claimsJws = jwtUtil.parseToken(accessToken);
        assertNotNull(claimsJws);

        assertEquals(name, claimsJws.getPayload().get(Const.USER_NAME));
        assertTrue(claimsJws.getPayload().getExpiration().after(new Date()));

    }

    @Test
    public void testGenerateRefreshToken() {

        String name = "John Doe";
        String refreshToken = jwtUtil.generateRefreshToken(name);

        assertNotNull(refreshToken);
        assertFalse(refreshToken.isEmpty());

        Jws<Claims> claimsJws = jwtUtil.parseToken(refreshToken);
        assertNotNull(claimsJws);

        assertEquals(name, claimsJws.getPayload().get(Const.USER_NAME));
        assertTrue(claimsJws.getPayload().getExpiration().after(new Date()));

    }

    @Test
    public void testIsTokenValid() {
        String name = "John Doe";
        String accessToken = jwtUtil.generateAccessToken(name);

        assertTrue(jwtUtil.isValid(accessToken));

        StringBuilder sb = new StringBuilder(accessToken);
        sb.deleteCharAt(sb.length() - 1);

        String newAccessToken = sb.toString();
        assertFalse(jwtUtil.isValid(newAccessToken));
    }

    @Test
    void testGetUserNameFromToken() {
        String username = "John Doe";
        String token = jwtUtil.generateAccessToken(username);

        String extractedUsername = jwtUtil.getUserNameFromToken(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void testParseToken() {
        String username = "John Doe";
        String token = jwtUtil.generateAccessToken(username);

        Jws<Claims> claimsJws = jwtUtil.parseToken(token);
        assertNotNull(claimsJws);
        assertEquals(username, claimsJws.getPayload().get(Const.USER_NAME, String.class));
    }


    @Test
    public void testParseInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(io.jsonwebtoken.MalformedJwtException.class, () -> {
            jwtUtil.parseToken(invalidToken);
        });
    }

}