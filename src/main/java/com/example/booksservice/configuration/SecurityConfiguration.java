package com.example.booksservice.configuration;

import com.example.booksservice.jwt.JWTTokenFilter;
import com.example.booksservice.jwt.JWTTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    private static final String ADMIN = "ADMIN";
    private static final String BOOK_ENDPOINT = "/book/**";
    private static final String DB = "/db/**";

    private static final String[] PUBLIC_URLS = {
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-resources/**",
            "configuration/**",
            "webjars/**",
            "/*.html",
            "/**/*.html",
            "/**/*.css",
            "/**/*.js"
    };


    @Bean
    public JWTTokenFilter jwtTokenFilter() {
        return new JWTTokenFilter(jwtTokenProvider);
    }

    @Autowired
    private JWTTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(HttpMethod.GET, PUBLIC_URLS).permitAll()
                        .antMatchers(DB).permitAll()
                        .antMatchers(BOOK_ENDPOINT).hasAuthority(ADMIN)
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

