package com.hemant.springsecurityfinalmvn.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JwtService {
    @Value("${security.jwt.secret}")
    private String secret;
    @Value("${security.jwt.access-exp-minutes:30}")
    private long accessExpMinutes;
    @Value("${security.jwt.refresh-exp-days:2}")
    private long refreshExpDays;
    @Value("${security.jwt.refresh-abs-exp-days:10}")
    private long refreshAbsExpDays;

    private Key key() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    private String tokenBuilder(Map<String, Object> claims, String username, Duration expiration) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(expiration)))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(key())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateAccessToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        claims.put("type", "access");
        return tokenBuilder(claims, user.getUsername(), Duration.ofMinutes(accessExpMinutes));
    }

    public String generateRefreshToken(UserDetails user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        claims.put("absoluteExp", Instant.now().plus(Duration.ofMinutes(5)).toEpochMilli());
        return tokenBuilder(claims, user.getUsername(), Duration.ofMinutes(refreshExpDays));
    }

    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        Claims claims = parseClaims(token);
        return user.getUsername().equals(claims.getSubject())
                && claims.getExpiration().after(new Date());
    }
}
