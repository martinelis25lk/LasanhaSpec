package br.com.lasanhaspec.carservice.domain.models;

import br.com.lasanhaspec.carservice.domain.enums.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class UserTest {

    @Test
    void getUsernameMustReturnEmailForSpringSecurityContract() {
        User user = new User();
        user.setEmail("piloto@lasanha.com");
        user.setUsername("piloto123");
        user.setRole(Role.ROLE_USER);

        // getUsername() eh o metodo do contrato UserDetails do Spring Security:
        // TEM que devolver o email, senao o login por email quebra.
        assertEquals("piloto@lasanha.com", user.getUsername());
    }

    @Test
    void getHandleMustReturnTheActualUsernameFieldNotTheEmail() {
        User user = new User();
        user.setEmail("piloto@lasanha.com");
        user.setUsername("piloto123");
        user.setRole(Role.ROLE_USER);

        // o "@handle" publico exibido em comentarios/perfil precisa vir daqui,
        // nunca de getUsername() -- senao vaza o email do usuario.
        assertEquals("piloto123", user.getHandle());
        assertNotEquals(user.getUsername(), user.getHandle());
    }
}