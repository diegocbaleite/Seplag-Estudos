package br.com.seplag.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private final String secret;
    private SecretKey key;
    public JwtService(@Value("${app.security.jwt-secret}") String secret) { this.secret = secret; }
    @PostConstruct
    void init() {
        if (secret.isBlank()) throw new IllegalStateException("APP_JWT_SECRET é obrigatório (Base64 de 32+ bytes)");
        key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
    public String emitir(String usuario, String tipo, long duracaoMs) {
        long agora = System.currentTimeMillis();
        return Jwts.builder().subject(usuario).claim("tipo", tipo)
                .issuedAt(new Date(agora)).expiration(new Date(agora + duracaoMs))
                .signWith(key).compact();
    }
    public String validar(String token, String tipo) {
        Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
        if (!tipo.equals(claims.get("tipo", String.class))) throw new JwtException("Tipo de token inválido");
        return claims.getSubject();
    }
}
