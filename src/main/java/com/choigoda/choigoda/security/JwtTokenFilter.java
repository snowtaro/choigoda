package com.choigoda.choigoda.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.choigoda.choigoda.security.JwtTokenProvider;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        // 1) /api/** 가 아니면 전부 스킵
        if (!path.startsWith("/api/")) {
            return true;
        }
        // 2) /api/auth/** (로그인·회원가입)도 스킵
        if (path.startsWith("/api/auth/")) {
            return true;
        }
        // 나머지 /api/** 요청만 필터 적용
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "유효하지 않은 JWT");
            return;
        }
        SecurityContextHolder.getContext()
                .setAuthentication(jwtTokenProvider.getAuthentication(token));
        filterChain.doFilter(request, response);
    }
}
