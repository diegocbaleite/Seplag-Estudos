package br.com.seplag.auth;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.*;

@Configuration
public class FilterRegistrationConfig {
    @Bean
    FilterRegistrationBean<JwtAuthenticationFilter> jwtRegistration(JwtAuthenticationFilter filter) {
        var bean = new FilterRegistrationBean<>(filter);
        bean.setEnabled(false); // executar exclusivamente na cadeia do Spring Security
        return bean;
    }
    @Bean
    FilterRegistrationBean<RateLimitFilter> rateRegistration(RateLimitFilter filter) {
        var bean = new FilterRegistrationBean<>(filter);
        bean.setEnabled(false);
        return bean;
    }
    @Bean
    FilterRegistrationBean<OriginFilter> originRegistration(OriginFilter filter) {
        var bean = new FilterRegistrationBean<>(filter);
        bean.setEnabled(false);
        return bean;
    }
}
