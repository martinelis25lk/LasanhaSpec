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
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserVehicleController.class)
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtService.class})
@TestPropertySource(properties = {
        "jwt.secret=dGVzdC1zZWNyZXQta2V5LXRlc3Qtc2VjcmV0LWtleS10ZXN0LXNlY3JldC1rZXk=",
        "jwt.expiration=3600000"
})
class UserVehicleAuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private UserVehicleService userVehicleService;

    private User regularUser;

    private static final String VALID_VEHICLE_PAYLOAD = """
            {
              "vehicleCatalogModelId": 1,
              "nickName": "Minha BMW",
              "currentHorsePower": 190,
              "currentWeight": 1300,
              "currentTorque": 250
            }
            """;

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

    // -------- POST /user-vehicles (cadastrar veiculo na garagem) --------

    @Test
    void anonymousCannotCreateVehicle() throws Exception {
        mockMvc.perform(post("/user-vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_VEHICLE_PAYLOAD))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanCreateVehicle() throws Exception {
        when(userVehicleService.createUserVehicle(any(), eq("user@test.com")))
                .thenReturn(1L);

        mockMvc.perform(post("/user-vehicles")
                        .header("Authorization", "Bearer " + tokenFor(regularUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_VEHICLE_PAYLOAD))
                .andExpect(status().isOk());
    }

    // -------- GET /user-vehicles/me --------

    @Test
    void anonymousCannotListOwnVehicles() throws Exception {
        mockMvc.perform(get("/user-vehicles/me"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserCanListOwnVehicles() throws Exception {
        when(userVehicleService.getVehiclesFromAuthenticatedUser("user@test.com"))
                .thenReturn(List.of());

        mockMvc.perform(get("/user-vehicles/me")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isOk());
    }

    // -------- DELETE /user-vehicles/{id} --------
    // A checagem "so o dono pode apagar" fica no UserVehicleService
    // (ja coberta em UserVehicleServiceTest); aqui garantimos que o
    // endpoint exige autenticacao antes de chegar no service.

    @Test
    void anonymousCannotDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/user-vehicles/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    void authenticatedUserReachesDeleteEndpoint() throws Exception {
        mockMvc.perform(delete("/user-vehicles/1")
                        .header("Authorization", "Bearer " + tokenFor(regularUser)))
                .andExpect(status().isNoContent());
    }

    // -------- POST /user-vehicles/{vehicleId}/images (upload) --------

    @Test
    void anonymousCannotUploadVehicleImage() throws Exception {
        mockMvc.perform(multipart("/user-vehicles/1/images")
                        .file("file", "conteudo-fake".getBytes()))
                .andExpect(status().isForbidden());
    }

    @Test
    void malformedTokenNeverReturnsServerError() throws Exception {
        mockMvc.perform(get("/user-vehicles/me")
                        .header("Authorization", "Bearer isso-nao-e-um-jwt-valido"))
                .andExpect(status().is4xxClientError());
    }
}