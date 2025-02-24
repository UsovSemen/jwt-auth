package org.practice.jwtauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    SecretKey secretKey = Jwts.SIG.HS256.key().build();
    private final long accessTokenValidity = 15 * 60 * 1000;
    private final long refreshTokenValidity = 60 * 60 * 60 * 1000;

    public boolean isValid(String authentication) {
        try {
            Jws<Claims> claimsJws = parseToken(authentication);
            return claimsJws.getPayload()
                    .getExpiration()
                    .after(new Date());
        } catch (io.jsonwebtoken.JwtException ex) {
            return false;
        }
    }

    public String generateAccessToken(String username) {
        return Jwts.builder()
                .header()
                .keyId("appId")
                .and()
                .subject("token company")
                .claim(Const.USER_NAME, username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .header()
                .keyId("appId")
                .and()
                .subject("token company")
                .claim(Const.USER_NAME, username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(secretKey)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        Jws<Claims> claimsJws = parseToken(token);
        return claimsJws.getPayload().get(Const.USER_NAME, String.class);
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token);
    }
}
