package com.surofu.madeinrussia.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
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
                        url = "https://exporteru.com"
                ),
                @Server(
                        description = "Development Environment",
                        url = "http://localhost:8080"
                )
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "Bearer Authentication",
                description = "JWT authentication with bearer token",
                scheme = "bearer",
                type = SecuritySchemeType.HTTP,
                bearerFormat = "JWT",
                in = SecuritySchemeIn.HEADER
        ),
        @SecurityScheme(
                name = "OAuth2 Google",
                description = "OAuth2 authentication with Google",
                type = SecuritySchemeType.OAUTH2,
                flows = @io.swagger.v3.oas.annotations.security.OAuthFlows(
                        authorizationCode = @io.swagger.v3.oas.annotations.security.OAuthFlow(
                                authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth",
                                tokenUrl = "https://oauth2.googleapis.com/token",
                                scopes = {
                                        @io.swagger.v3.oas.annotations.security.OAuthScope(
                                                name = "email",
                                                description = "Email access"
                                        )
                                }
                        )
                )
        )
})
public class OpenApiConfig {
}