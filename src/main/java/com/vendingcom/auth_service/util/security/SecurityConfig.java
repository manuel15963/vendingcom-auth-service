package com.vendingcom.auth_service.util.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final SecurityErrorWriter securityErrorWriter;
    private final List<String> allowedOrigins;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityErrorWriter securityErrorWriter,
            @Value("${cors.allowed-origins}") List<String> allowedOrigins
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.securityErrorWriter = securityErrorWriter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(cors -> cors.disable())
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
                         * CORS PREFLIGHT
                         * ============================================================
                         * Permite que el navegador valide CORS antes de hacer POST/GET.
                         */
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()

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
                         * RESET DE CONTRASEÑA POR ADMIN
                         * ============================================================
                         * Solo ADMIN puede restablecer la contraseña de otro usuario.
                         */
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/auth/password/users/**").hasRole("ADMIN")

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
                        .anyExchange().authenticated()
                )

                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();

        /*
         * ============================================================
         * ORÍGENES PERMITIDOS
         * ============================================================
         * Se configuran por variable de entorno (cors.allowed-origins),
         * separados por coma. Ej: http://localhost:8100,https://vendingcom-app.onrender.com
         */
        config.setAllowedOrigins(allowedOrigins);

        /*
         * ============================================================
         * MÉTODOS PERMITIDOS
         * ============================================================
         */
        config.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        /*
         * ============================================================
         * HEADERS PERMITIDOS
         * ============================================================
         */
        config.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "X-Requested-With"
        ));

        /*
         * ============================================================
         * HEADERS EXPUESTOS AL FRONT
         * ============================================================
         */
        config.setExposedHeaders(Arrays.asList(
                "Authorization"
        ));

        /*
         * Si usas Authorization Bearer, puedes dejarlo en true.
         * No uses "*" en allowedOrigins cuando allowCredentials es true.
         */
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsWebFilter(source);
    }
}