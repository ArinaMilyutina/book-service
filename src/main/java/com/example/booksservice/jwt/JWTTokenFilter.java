package com.example.booksservice.jwt;

import lombok.SneakyThrows;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTTokenFilter extends OncePerRequestFilter {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JWTTokenProvider jwtTokenProvider;

    public JWTTokenFilter(JWTTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }


    private String getToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);
        if (header != null && header.startsWith(BEARER)) {
            return header.substring(BEARER.length());
        }
        return null;
    }

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getToken(request);
        if (token != null) {
            var authentication = jwtTokenProvider.validateToken(token);
            if (authentication != null) {
                org.springframework.security.core.context.SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);

    }
}
