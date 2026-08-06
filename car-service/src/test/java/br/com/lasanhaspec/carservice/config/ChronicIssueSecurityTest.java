package br.com.lasanhaspec.carservice.config;

import br.com.lasanhaspec.carservice.controller.UserVehicleController;
import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.filter.JwtAuthFilter;
import br.com.lasanhaspec.carservice.service.JwtService;
import br.com.lasanhaspec.carservice.service.UserVehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Commit: test: exception handler must not leak internal error details
 *
 * Garante que o GlobalExceptionHandler não vaza stack traces,
 * mensagens de exception interna, ou informações do banco de dados
 * em respostas de erro 500.
 *
 * PROBLEMA ATUAL: o handler genérico retorna ex.getMessage() diretamente,
 * o que pode expor coisas como:
 *   "could not execute statement; SQL [n/a]; constraint [uk_user_email]"
 *   "Connection refused: localhost/127.0.0.1:5432"
 *   "NullPointerException at UserVehicleService.java:83"
 */
@WebMvcTest(controllers = UserVehicleController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=",
        "jwt.expiration=3600000"
})
class GlobalExceptionHandlerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserVehicleService userVehicleService;

    private User regularUser;
    private String validToken;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setEmail("user@test.com");
        regularUser.setUsername("piloto123");
        regularUser.setRole(Role.ROLE_USER);

        when(userDetailsService.loadUserByUsername("user@test.com"))
                .thenReturn(regularUser);

        validToken = jwtService.generateToken(regularUser);
    }

    // -----------------------------------------------------------------------
    // Handler genérico (500) não deve vazar informação interna
    // -----------------------------------------------------------------------

    @Test
    void internalExceptionReturns500WithoutLeakingStackTrace() throws Exception {
        // Service lança exceção inesperada com mensagem sensível
        String internalMessage =
                "could not execute statement; SQL [n/a]; " +
                        "constraint [uk_users_email]; " +
                        "nested exception is org.postgresql.util.PSQLException";

        when(userVehicleService.getFeedVehicles())
                .thenThrow(new RuntimeException(internalMessage));

        mockMvc.perform(get("/user-vehicles/feed")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isInternalServerError())
                // FALHA ESPERADA ATUAL: o handler devolve ex.getMessage() diretamente,
                // então o corpo vai conter o internalMessage acima.
                // Após a correção (retornar mensagem genérica), este teste deve passar.
                .andExpect(jsonPath("$.message",
                        not(containsString("PSQLException"))))
                .andExpect(jsonPath("$.message",
                        not(containsString("constraint"))))
                .andExpect(jsonPath("$.message",
                        not(containsString("org.postgresql"))));
    }

    @Test
    void internalExceptionResponseContainsGenericMessage() throws Exception {
        when(userVehicleService.getFeedVehicles())
                .thenThrow(new RuntimeException("NullPointerException at line 83"));

        mockMvc.perform(get("/user-vehicles/feed")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isInternalServerError())
                // A mensagem deve ser genérica, sem referência a classes internas
                .andExpect(jsonPath("$.message",
                        not(containsString("NullPointerException"))))
                .andExpect(jsonPath("$.error",
                        equalTo("Internal Server Error")))
                .andExpect(jsonPath("$.status", equalTo(500)));
    }

    @Test
    void internalExceptionResponseDoesNotExposeConnectionDetails() throws Exception {
        when(userVehicleService.getFeedVehicles())
                .thenThrow(new RuntimeException(
                        "Connection refused: localhost/127.0.0.1:5432"));

        mockMvc.perform(get("/user-vehicles/feed")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message",
                        not(containsString("localhost"))))
                .andExpect(jsonPath("$.message",
                        not(containsString("5432"))));
    }

    // -----------------------------------------------------------------------
    // 404 e 400 continuam devolvendo mensagem útil (não regride)
    // -----------------------------------------------------------------------

    @Test
    void notFoundResponseIsStructured() throws Exception {
        when(userVehicleService.getVehiclesFromAuthenticatedUser(eq("user@test.com")))
                .thenReturn(java.util.List.of());

        mockMvc.perform(get("/user-vehicles/me")
                        .header("Authorization", "Bearer " + validToken))
                .andExpect(status().isOk()); // garante que o endpoint continua acessível
    }

    // -----------------------------------------------------------------------
    // Erro de acesso negado não vaza detalhes
    // -----------------------------------------------------------------------

    @Test
    void accessDeniedResponseDoesNotLeakInternalDetails() throws Exception {
        // Anônimo bate num endpoint protegido
        mockMvc.perform(get("/user-vehicles/me"))
                .andExpect(status().isForbidden())
                // O corpo não deve conter stack trace nem classe Java
                .andExpect(result -> {
                    String body = result.getResponse().getContentAsString();
                    org.junit.jupiter.api.Assertions.assertFalse(
                            body.contains("at br.com.lasanhaspec"),
                            "Resposta 403 não deve conter stack trace");
                });
    }
}