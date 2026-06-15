package com.vendingcom.auth_service.util.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vendingComAuthOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("VendingCom Auth Service API")
                        .version("1.0.0")
                        .description("""
                                Documentación técnica del microservicio de autenticación de VendingCom.

                                Incluye:
                                - Login con JWT
                                - Consulta de usuario autenticado
                                - Gestión de usuarios
                                - Consulta de roles
                                - Auditoría
                                - Cambio de contraseña
                                - Recuperación de contraseña por correo
                                """)
                        .contact(new Contact()
                                .name("VendingCom")
                                .email("adolfo.berrocal@vallegrande.edu.pe"))
                        .license(new License()
                                .name("Proyecto universitario")
                                .url("https://vendingcom.local")))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}