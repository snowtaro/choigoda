package com.choigoda.choigoda.security;

import java.util.Date;
import java.util.List;
import java.security.Key;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;                   // Base64 인코딩된 시크릿

    @Value("${jwt.expiration}")
    private long validityInMilliseconds;        // 만료시간(ms)

    private Key key;                            // HMAC-SHA 키 객체

    /** 0) secretKey → Key 로 변환 (앱 시작 시 한 번만) */
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    /** 1) 토큰 생성 */
    public String createToken(Authentication auth) {
        String username = auth.getName();
        Date now = new Date();
        Date expiry = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiry)
                // Deprecated API 대신 Key 객체 + 알고리즘 순으로 지정
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 2) 토큰으로부터 Authentication 생성 */
    public Authentication getAuthentication(String token) {
        String username = Jwts.parserBuilder()
                .setSigningKey(key)      // parser() 대신 parserBuilder()
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();

        return new UsernamePasswordAuthenticationToken(username, "", List.of());
    }

    /** 3) HTTP 헤더에서 “Bearer ” 토큰 부분만 추출 */
    public String resolveToken(HttpServletRequest req) {
        String bearer = req.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

    /** 4) 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
