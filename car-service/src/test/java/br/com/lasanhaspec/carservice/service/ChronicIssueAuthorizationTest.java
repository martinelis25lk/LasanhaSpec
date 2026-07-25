package br.com.lasanhaspec.carservice.config;

import br.com.lasanhaspec.carservice.controller.ChronicIssueController;
import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.User;
import br.com.lasanhaspec.carservice.filter.JwtAuthFilter;
import br.com.lasanhaspec.carservice.service.ChronicIssueService;
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

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Roda a cadeia real de Spring Security (JwtAuthFilter + SecurityConfig).
// Sem "addFilters = false" de propósito: é isso que valida as regras de
// autorização de verdade, não só o comportamento do service.
@WebMvcTest(controllers = ChronicIssueController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=",
        "jwt.expiration=3600000"
})
class ChronicIssueAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private ChronicIssueService chronicIssueService;

    private User regularUser;
    private User adminUser;

    @BeforeEach
    void setUp() {
        regularUser = new User();
        regularUser.setId(1L);
        regularUser.setEmail("user@test.com");
        regularUser.setRole(Role.ROLE_USER);

        adminUser = new User();
        adminUser.setId(2L);
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(Role.ROLE_ADMIN);

        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(regularUser);
        when(userDetailsService.loadUserByUsername("admin@test.com")).thenReturn(adminUser);
    }

    private String tokenFor(User user) {
        return jwtService.generateToken(user);
    }

    @Test
    void regularUserCannotDeleteChronicIssue() throws Exception {
        mockMvc.perform(delete("/chronic-issues/1")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanDeleteChronicIssue() throws Exception {
        mockMvc.perform(delete("/chronic-issues/1")
                        .header("Authorization", "Bearer " + tokenFor(adminUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void anonymousCannotDeleteChronicIssue() throws Exception {
        mockMvc.perform(delete("/chronic-issues/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void regularUserCannotUpdateChronicIssue() throws Exception {
        mockMvc.perform(put("/chronic-issues/1")
                        .header("Authorization", "Bearer " + tokenFor(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void regularUserCannotApproveChronicIssue() throws Exception {
        mockMvc.perform(patch("/chronic-issues/1/approve")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanApproveChronicIssue() throws Exception {
        mockMvc.perform(patch("/chronic-issues/1/approve")
                        .header("Authorization", "Bearer " + tokenFor(adminUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void malformedTokenNeverReturnsServerError() throws Exception {
        mockMvc.perform(get("/chronic-issues")
                        .header("Authorization", "Bearer isso-nao-e-um-jwt-valido"))
                .andExpect(status().is4xxClientError());
    }
}