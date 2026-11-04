package br.com.seplag.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;

@OpenAPIDefinition(info = @Info(title = "SEPLAG - API de Artistas e Álbuns", version = "v1"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "bearerAuth")
@org.springframework.context.annotation.Configuration
public class OpenApiConfig {}

