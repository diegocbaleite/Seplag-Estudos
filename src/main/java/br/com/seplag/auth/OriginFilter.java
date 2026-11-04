package br.com.seplag.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class OriginFilter extends OncePerRequestFilter {
    private final String allowedOrigin;
    private final String serviceOrigin;
    public OriginFilter(@Value("${app.origin}") String allowedOrigin,
                        @Value("${app.service-origin}") String serviceOrigin) {
        this.allowedOrigin = allowedOrigin;
        this.serviceOrigin = serviceOrigin;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        String origin = req.getHeader("Origin");
        if (origin != null && !origin.equals(allowedOrigin) && !origin.equals(serviceOrigin)) {
            res.sendError(HttpServletResponse.SC_FORBIDDEN, "Domínio de origem não autorizado");
            return;
        }
        chain.doFilter(req, res);
    }
}
