package br.com.seplag.config;

import br.com.seplag.security.JwtAuthenticationFilter;
import br.com.seplag.security.OriginFilter;
import br.com.seplag.security.RateLimitFilter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;

@Configuration
public class SegurancaConfig {
    @Bean
    SecurityFilterChain security(HttpSecurity http, JwtAuthenticationFilter jwt,
                                  RateLimitFilter rate, OriginFilter origin) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(a -> a
                .requestMatchers("/api/v1/auth/**", "/actuator/health/**", "/swagger-ui.html",
                                 "/swagger-ui/**", "/v3/api-docs/**", "/ws/**").permitAll()
                .anyRequest().authenticated())
            .exceptionHandling(e -> e.authenticationEntryPoint((req,res,ex) -> res.sendError(401, "Não autenticado")));
        http.addFilterBefore(jwt, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(rate, JwtAuthenticationFilter.class);
        http.addFilterBefore(origin, JwtAuthenticationFilter.class);
        return http.build();
    }
    @Bean
    CorsConfigurationSource corsSource(@Value("${app.origin}") String allowedOrigin,
                                       @Value("${app.service-origin}") String serviceOrigin) {
        CorsConfiguration c = new CorsConfiguration();
        c.setAllowedOrigins(List.of(allowedOrigin, serviceOrigin));
        c.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        c.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        c.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", c);
        return source;
    }
}
