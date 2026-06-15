package com.vendingcom.auth_service.util.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import java.util.Arrays;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorWriter securityErrorWriter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityErrorWriter securityErrorWriter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityErrorWriter = securityErrorWriter;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint((exchange, exception) ->
                                securityErrorWriter.write(
                                        exchange,
                                        HttpStatus.UNAUTHORIZED,
                                        "UNAUTHORIZED",
                                        "Token no enviado, inválido o expirado."
                                )
                        )
                        .accessDeniedHandler((exchange, exception) ->
                                securityErrorWriter.write(
                                        exchange,
                                        HttpStatus.FORBIDDEN,
                                        "FORBIDDEN",
                                        "No tiene permisos para acceder a este recurso."
                                )
                        )
                )

                .authorizeExchange(exchange -> exchange

                        /*
                         * ============================================================
                         * ENDPOINTS PÚBLICOS
                         * ============================================================
                         * No requieren token JWT.
                         */
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/password/recovery/request").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/password/recovery/confirm").permitAll()

                        /*
                         * ============================================================
                         * SWAGGER / OPENAPI
                         * ============================================================
                         * Permite visualizar y probar la documentación de la API.
                         */
                        // Swagger público

                        .pathMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/swagger-resources/**"

                        ).permitAll()

                        /*
                         * ============================================================
                         * MONITOREO / DEBUG
                         * ============================================================
                         */
                        .pathMatchers("/actuator/health").permitAll()
                        .pathMatchers("/debug/**").permitAll()

                        /*
                         * ============================================================
                         * ENDPOINTS AUTENTICADOS
                         * ============================================================
                         * Requieren token JWT válido.
                         */
                        .pathMatchers(HttpMethod.GET, "/api/v1/auth/me").authenticated()
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/password/me").authenticated()

                        /*
                         * ============================================================
                         * AUDITORÍA
                         * ============================================================
                         * Solo ADMIN puede consultar eventos auditados.
                         */
                        .pathMatchers("/api/v1/auth/audit-logs/**").hasRole("ADMIN")

                        /*
                         * ============================================================
                         * ROLES
                         * ============================================================
                         * ADMIN y SUPERVISOR pueden consultar roles.
                         */
                        .pathMatchers(HttpMethod.GET, "/api/v1/auth/roles/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        /*
                         * ============================================================
                         * USUARIOS - CONSULTA
                         * ============================================================
                         * ADMIN y SUPERVISOR pueden listar y consultar usuarios.
                         */
                        .pathMatchers(HttpMethod.GET, "/api/v1/auth/users/**")
                        .hasAnyRole("ADMIN", "SUPERVISOR")

                        /*
                         * ============================================================
                         * USUARIOS - ADMINISTRACIÓN
                         * ============================================================
                         * Solo ADMIN puede crear, actualizar, activar, bloquear o eliminar usuarios.
                         */
                        .pathMatchers(HttpMethod.POST, "/api/v1/auth/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/auth/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/users/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/auth/users/**").hasRole("ADMIN")

                        /*
                         * ============================================================
                         * CUALQUIER OTRO ENDPOINT
                         * ============================================================
                         * Por defecto requiere autenticación.
                         */
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyExchange().authenticated()
                )

                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        // Permitir orígenes de desarrollo del frontend (ajusta según entorno)
        config.setAllowedOrigins(Arrays.asList(
                "http://localhost:4200",
                "http://127.0.0.1:4200",
                "http://localhost:8100",
                "http://127.0.0.1:8100"
        ));
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}