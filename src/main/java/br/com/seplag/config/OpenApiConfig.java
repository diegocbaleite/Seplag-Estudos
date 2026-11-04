package br.com.seplag.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI seplagOpenApi() {
        return new OpenAPI()
            .info(new Info().title("SEPLAG - API de Artistas e Álbuns").version("v1"))
            .components(new Components().addSecuritySchemes("bearerAuth",
                new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }

    @Bean
    public OpenApiCustomizer manterLoginERenovacaoPublicos() {
        return openApi -> {
            if (openApi.getPaths() == null) {
                return;
            }
            for (String caminho : new String[]{"/api/v1/auth/login", "/api/v1/auth/refresh"}) {
                var operacao = openApi.getPaths().get(caminho);
                if (operacao != null && operacao.getPost() != null) {
                    operacao.getPost().setSecurity(Collections.emptyList());
                }
            }
        };
    }
}
