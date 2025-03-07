package org.practice.jwtauth.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtUtil {

    SecretKey secretKey = Jwts.SIG.HS256.key().build();

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
                .claim(Params.USER_NAME.getValue(), username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(15).toMillis()))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .header()
                .keyId("appId")
                .and()
                .subject("token company")
                .claim(Params.USER_NAME.getValue(), username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + Duration.ofMinutes(60).toMillis()))
                .signWith(secretKey)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        Jws<Claims> claimsJws = parseToken(token);
        return claimsJws.getPayload().get(Params.USER_NAME.getValue(), String.class);
    }

    public Jws<Claims> parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build().parseSignedClaims(token);
    }
}
