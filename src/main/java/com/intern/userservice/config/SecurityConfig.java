package com.intern.userservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.security.oauth2.jwt.NimbusJwtDecoder.withJwkSetUri;

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

    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Example: extract roles from Keycloak's "realm_access.roles" claim
            Object realmAccess = jwt.getClaim("realm_access");
            if (realmAccess instanceof Map) {
                Object roles = ((Map<?,?>) realmAccess).get("roles");
                if (roles instanceof Collection) {
                    Collection<?> roleList = (Collection<?>) roles;
                    return roleList.stream()
                            .map(Object::toString)
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                            .collect(Collectors.toSet());
                }
            }
            return java.util.Collections.emptySet();
        });
        return converter;
    }

    @Bean
    public JwtDecoder jwtDecoder(Environment env) {
        // Prefer issuer-uri; Spring will configure NimbusJwtDecoder automatically if you expose
        // spring.security.oauth2.resourceserver.jwt.issuer-uri in properties.
        String issuer = env.getProperty("spring.security.oauth2.resourceserver.jwt.issuer-uri");
        if (issuer != null) {
            return withJwkSetUri(
                    issuer + "/protocol/openid-connect/certs" // optional if not using issuer discovery
            ).build();
        }
        String jwkSetUri = env.getProperty("spring.security.oauth2.resourceserver.jwt.jwk-set-uri");
        if (jwkSetUri != null) {
            return withJwkSetUri(jwkSetUri).build();
        }
        throw new IllegalStateException("No jwt issuer-uri or jwk-set-uri configured");
    }
}
