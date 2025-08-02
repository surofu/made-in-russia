package com.surofu.madeinrussia.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                contact = @Contact(
                        name = "Stanislav",
                        email = "stanislavswork@gmail.com",
                        url = "https://github.com/surofu"
                ),
                title = "OpenApi specification - Stanislav",
                description = "OpenApi for MadeInRussia",
                version = "1.0"
        ),
        servers = {
                @Server(
                        description = "Ultrahost Environment",
                        url = "http://181.215.18.219"
                ),
                @Server(
                        description = "Development Environment",
                        url = "http://localhost:8080"
                )
        }
)
@SecurityScheme(
        name = "Bearer Authentication",
        description = "Authentication with JWT",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
