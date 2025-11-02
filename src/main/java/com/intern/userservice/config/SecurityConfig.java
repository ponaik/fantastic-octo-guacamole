package com.intern.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

@Profile("!test")
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        JwtGrantedAuthoritiesConverter scopeConverter = new JwtGrantedAuthoritiesConverter();
        scopeConverter.setAuthorityPrefix("SCOPE_");

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>(scopeConverter.convert(jwt));
            authorities.addAll(extractRealmRoles(jwt));

            return authorities;
        });
        return converter;
    }

    private Collection<SimpleGrantedAuthority> extractRealmRoles(Jwt jwt) {
        Object realmAccess = jwt.getClaim("realm_access");
        if (!(realmAccess instanceof Map)) return Collections.emptyList();
        Map<String, Object> realm = (Map<String, Object>) realmAccess;
        Object roles = realm.get("roles");
        if (!(roles instanceof Collection)) return Collections.emptySet();
        return ((Collection<?>) roles).stream()
                .map(Object::toString)
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                .collect(Collectors.toSet());
    }

    @Bean
    public JwtDecoder jwtDecoder(Environment env) {
        String issuer = env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        if (issuer != null) {
            return withJwkSetUri(
                    issuer + "/protocol/openid-connect/certs"
            ).build();
        }
        String jwkSetUri = env.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
        if (jwkSetUri != null) {
            return withJwkSetUri(jwkSetUri).build();
        }
        throw new IllegalStateException("No jwt issuer-uri or jwk-set-uri configured");
    }
}
