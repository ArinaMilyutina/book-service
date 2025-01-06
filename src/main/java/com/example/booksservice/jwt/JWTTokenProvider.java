package com.example.booksservice.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTTokenProvider {
    private static final String LIBRARY2="library2";
    private static final String USER_ID="userId";
    private static final String ROLES="roles";
    private final static String INVALID_TOKEN = "JWT token is expired or invalid";
    @Value("${jwt.token.secret}")
    private String jwtSecret;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder().encodeToString(jwtSecret.getBytes());
    }

    public Authentication validateToken(String token) throws JWTAuthenticationException {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer(LIBRARY2)
                .build();
        try {
            DecodedJWT jwt = verifier.verify(token);
            String userId = jwt.getClaim(USER_ID).asString();
            List<String> roles = jwt.getClaim(ROLES).asList(String.class);
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(userId, token, authorities);
        } catch (JWTVerificationException exception) {
            throw new JWTAuthenticationException(INVALID_TOKEN);
        }
    }
}
