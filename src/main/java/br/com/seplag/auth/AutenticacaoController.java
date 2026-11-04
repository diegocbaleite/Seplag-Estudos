package br.com.seplag.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/v1/auth")
public class AutenticacaoController {
    private final JwtService jwt;
    private final String usuario;
    private final String senha;

    public AutenticacaoController(JwtService jwt,
        @Value("${app.security.user}") String usuario,
        @Value("${app.security.password}") String senha) {
        if (senha.isBlank()) throw new IllegalStateException("APP_AUTH_PASSWORD é obrigatório");
        this.jwt = jwt; this.usuario = usuario; this.senha = senha;
    }
    public record Credenciais(@NotBlank String usuario, @NotBlank String senha) {}
    public record Renovacao(@NotBlank String refreshToken) {}
    public record Tokens(String accessToken, String refreshToken, String tipo, long expiraEmSegundos) {}
    @PostMapping("/login")
    public Tokens login(@Valid @RequestBody Credenciais dados) {
        boolean usuarioOk = MessageDigest.isEqual(usuario.getBytes(StandardCharsets.UTF_8), dados.usuario().getBytes(StandardCharsets.UTF_8));
        boolean senhaOk = MessageDigest.isEqual(senha.getBytes(StandardCharsets.UTF_8), dados.senha().getBytes(StandardCharsets.UTF_8));
        if (!usuarioOk || !senhaOk) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        return new Tokens(jwt.emitir(usuario, "access", 300_000), jwt.emitir(usuario, "refresh", 86_400_000), "Bearer", 300);
    }
    @PostMapping("/refresh")
    public Tokens renovar(@Valid @RequestBody Renovacao dados) {
        try {
            String nome = jwt.validar(dados.refreshToken(), "refresh");
            if (!usuario.equals(nome)) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token inválido");
            return new Tokens(jwt.emitir(nome, "access", 300_000), jwt.emitir(nome, "refresh", 86_400_000), "Bearer", 300);
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token inválido");
        }
    }
}
