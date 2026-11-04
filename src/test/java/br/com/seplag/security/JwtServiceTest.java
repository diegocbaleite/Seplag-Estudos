package br.com.seplag.security;

import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    private JwtService servico() {
        var segredo = Base64.getEncoder().encodeToString("chave-secreta-de-testes-com-trinta-e-dois-bytes".getBytes(StandardCharsets.UTF_8));
        var jwt = new JwtService(segredo);
        jwt.init();
        return jwt;
    }
    @Test
    void aceitaTokenDeAcessoValido() {
        var jwt = servico();
        String access = jwt.emitir("usuario", "access", 300_000);
        assertEquals("usuario", jwt.validar(access, "access"));
    }
    @Test
    void naoAceitaRefreshComoAccess() {
        var jwt = servico();
        String refresh = jwt.emitir("usuario", "refresh", 86_400_000);
        assertThrows(JwtException.class, () -> jwt.validar(refresh, "access"));
    }
    @Test
    void rejeitaTokenExpirado() {
        var jwt = servico();
        String token = jwt.emitir("usuario", "access", -1000);
        assertThrows(JwtException.class, () -> jwt.validar(token, "access"));
    }
}
