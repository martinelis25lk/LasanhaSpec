package br.com.lasanhaspec.carservice.config;

import br.com.lasanhaspec.carservice.controller.CommentsController;
import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.dto.CommentDTO;
import br.com.lasanhaspec.carservice.filter.JwtAuthFilter;
import br.com.lasanhaspec.carservice.service.CommentsService;
import br.com.lasanhaspec.carservice.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Cadeia real de Spring Security (JwtAuthFilter + SecurityConfig), sem
// addFilters = false -- e' isso que valida as regras de autorizacao de
// verdade, nao so o que o service faria se fosse chamado.
@WebMvcTest(controllers = CommentsController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=",
        "jwt.expiration=3600000"
})
class CommentsAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private CommentsService commentsService;

    private User regularUser;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setEmail("user@test.com");
        regularUser.setUsername("piloto123");
        regularUser.setRole(Role.ROLE_USER);

        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(regularUser);
    }

    private String tokenFor(User user) {
        return jwtService.generateToken(user);
    }

    // -------- GET (listar comentarios) --------

    @Test
    void anonymousCannotListComments() throws Exception {
        mockMvc.perform(get("/chronic-issues/1/comments"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanListComments() throws Exception {
        when(commentsService.listComments(1L)).thenReturn(List.of());

        mockMvc.perform(get("/chronic-issues/1/comments")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isOk());
    }

    // -------- POST (criar comentario) --------

    @Test
    void anonymousCannotCreateComment() throws Exception {
        mockMvc.perform(post("/chronic-issues/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"aconteceu comigo tambem\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanCreateComment() throws Exception {
        when(commentsService.createComment(eq(1L), any(), eq("user@test.com")))
                .thenReturn(42L);

        mockMvc.perform(post("/chronic-issues/1/comments")
                        .header("Authorization", "Bearer " + tokenFor(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"content\":\"aconteceu comigo tambem\"}"))
                .andExpect(status().isOk());
    }

    // -------- DELETE (apagar comentario) --------
    // A checagem de "so o autor ou admin pode apagar" fica no CommentsService
    // (ja coberta em CommentsServiceTest); aqui so garantimos que o endpoint
    // exige autenticacao antes de sequer chegar no service.

    @Test
    void anonymousCannotDeleteComment() throws Exception {
        mockMvc.perform(delete("/chronic-issues/1/comments/5"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserReachesDeleteEndpoint() throws Exception {
        mockMvc.perform(delete("/chronic-issues/1/comments/5")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void malformedTokenNeverReturnsServerError() throws Exception {
        mockMvc.perform(get("/chronic-issues/1/comments")
                        .header("Authorization", "Bearer isso-nao-e-um-jwt-valido"))
                .andExpect(status().is4xxClientError());
    }
}