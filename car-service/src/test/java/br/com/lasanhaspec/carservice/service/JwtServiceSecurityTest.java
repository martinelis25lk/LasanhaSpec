package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Commit: test: validate JWT tampering and expiration edge cases
 *
 * Testa que o JwtService rejeita tokens adulterados, expirados e
 * com assinaturas de chaves diferentes.
 */
class JwtServiceSecurityTest {

    // Chave válida de 256 bits em Base64 (igual à usada nos outros testes)
    private static final String VALID_SECRET =
            "dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=";

    // Chave diferente — simula um atacante com outra chave
    private static final String DIFFERENT_SECRET =
            "YXR0YWNrZXIta2V5LWF0dGFja2VyLWtleS1hdHRhY2tlci1rZXktYXR0YWNrZXI=";

    private JwtService jwtService;
    private JwtService differentKeyService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", VALID_SECRET);
        ReflectionTestUtils.setField(jwtService, "expiration", 3_600_000L); // 1 hora

        differentKeyService = new JwtService();
        ReflectionTestUtils.setField(differentKeyService, "secretKey", DIFFERENT_SECRET);
        ReflectionTestUtils.setField(differentKeyService, "expiration", 3_600_000L);

        user = new User();
        user.setId(1L);
        user.setEmail("piloto@test.com");
        user.setUsername("piloto123");
        user.setRole(Role.ROLE_USER);
    }

    // -----------------------------------------------------------------------
    // token válido
    // -----------------------------------------------------------------------

    @Test
    void validTokenIsAccepted() {
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.isTokenValid(token, user));
        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void extractsCorrectEmailFromToken() {
        String token = jwtService.generateToken(user);

        assertEquals("piloto@test.com", jwtService.extractUserName(token));
    }

    // -----------------------------------------------------------------------
    // token adulterado na payload (ataque de alteração de claims)
    // -----------------------------------------------------------------------

    @Test
    void tamperingPayloadMakesTokenInvalid() {
        String token = jwtService.generateToken(user);

        // Divide o JWT em header.payload.signature
        String[] parts = token.split("\\.");
        assertTrue(parts.length == 3, "JWT deve ter 3 partes");

        // Substitui a payload por uma versão modificada (em Base64)
        // Qualquer alteração no payload invalida a assinatura
        String fakePart = parts[1] + "X"; // corrompe a payload
        String tamperedToken = parts[0] + "." + fakePart + "." + parts[2];

        assertThrows(JwtException.class,
                () -> jwtService.extractUserName(tamperedToken),
                "Token com payload adulterada deve lançar JwtException");
    }

    @Test
    void tamperingSignatureMakesTokenInvalid() {
        String token = jwtService.generateToken(user);
        String[] parts = token.split("\\.");

        // Corrompe apenas a assinatura
        String tamperedToken = parts[0] + "." + parts[1] + ".assinaturaFalsa123";

        assertThrows(JwtException.class,
                () -> jwtService.extractUserName(tamperedToken),
                "Token com assinatura adulterada deve lançar JwtException");
    }

    // -----------------------------------------------------------------------
    // token assinado com chave diferente (ataque de chave própria)
    // -----------------------------------------------------------------------

    @Test
    void tokenSignedWithDifferentKeyIsRejected() {
        // Atacante gera um token com a própria chave
        String attackerToken = differentKeyService.generateToken(user);

        // Nosso serviço deve rejeitar
        assertThrows(JwtException.class,
                () -> jwtService.extractUserName(attackerToken),
                "Token assinado com chave diferente deve ser rejeitado");
    }

    @Test
    void tokenSignedWithDifferentKeyFailsValidation() {
        String attackerToken = differentKeyService.generateToken(user);

        // isTokenValid deve retornar false (ou lançar exceção) — nunca true
        try {
            boolean valid = jwtService.isTokenValid(attackerToken, user);
            assertFalse(valid, "Token de outra chave nunca deve ser válido");
        } catch (JwtException e) {
            // Também aceitável: lançar exceção ao tentar verificar
        }
    }

    // -----------------------------------------------------------------------
    // token expirado
    // -----------------------------------------------------------------------

    @Test
    void expiredTokenIsDetected() {
        JwtService expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "secretKey", VALID_SECRET);
        // Expiração no passado: -1 segundo
        ReflectionTestUtils.setField(expiredService, "expiration", -1_000L);

        String expiredToken = expiredService.generateToken(user);

        assertTrue(jwtService.isTokenExpired(expiredToken),
                "Token com expiração no passado deve ser detectado como expirado");
    }

    @Test
    void expiredTokenFailsValidation() {
        JwtService expiredService = new JwtService();
        ReflectionTestUtils.setField(expiredService, "secretKey", VALID_SECRET);
        ReflectionTestUtils.setField(expiredService, "expiration", -1_000L);

        String expiredToken = expiredService.generateToken(user);

        assertFalse(jwtService.isTokenValid(expiredToken, user),
                "Token expirado deve falhar na validação");
    }

    // -----------------------------------------------------------------------
    // token malformado (não é JWT)
    // -----------------------------------------------------------------------

    @Test
    void completelyInvalidTokenThrows() {
        assertThrows(JwtException.class,
                () -> jwtService.extractUserName("isso.nao.e.um.jwt"));
    }

    @Test
    void emptyTokenThrows() {
        assertThrows(Exception.class,
                () -> jwtService.extractUserName(""));
    }

    @Test
    void nullTokenThrows() {
        assertThrows(Exception.class,
                () -> jwtService.extractUserName(null));
    }

    // -----------------------------------------------------------------------
    // isTokenValid: usuário errado (token de outro usuário)
    // -----------------------------------------------------------------------

    @Test
    void tokenOfUserAIsInvalidForUserB() {
        User userB = new User();
        userB.setId(2L);
        userB.setEmail("outro@test.com");
        userB.setUsername("outro");
        userB.setRole(Role.ROLE_USER);

        String tokenForA = jwtService.generateToken(user); // token do user A

        // Tentativa de usar o token de A para autenticar B
        assertFalse(jwtService.isTokenValid(tokenForA, userB),
                "Token de user A não deve ser válido para user B");
    }
}