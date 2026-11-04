package br.com.seplag.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final Map<String, Deque<Long>> acessos = new ConcurrentHashMap<>();
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !path.startsWith("/api/v1/") || path.startsWith("/api/v1/auth/");
    }
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            AtomicBoolean permitido = new AtomicBoolean(true);
            long agora = System.currentTimeMillis();
            acessos.compute(auth.getName(), (nome, janela) -> {
                if (janela == null) janela = new ArrayDeque<>();
                while (!janela.isEmpty() && janela.peekFirst() <= agora - 60_000) janela.removeFirst();
                if (janela.size() >= 10) permitido.set(false);
                else janela.addLast(agora);
                return janela;
            });
            if (!permitido.get()) {
                res.setStatus(429);
                res.setHeader("Retry-After", "60");
                res.setContentType("application/json");
                res.getWriter().write("{\"erro\":\"Limite de 10 requisicoes por minuto excedido\"}");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
