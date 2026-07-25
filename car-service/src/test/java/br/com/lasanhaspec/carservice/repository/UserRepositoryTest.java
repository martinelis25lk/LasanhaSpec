package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.enums.Role;
import br.com.lasanhaspec.carservice.domain.models.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldFindUserByEmail() {
        User user = new User();
        user.setEmail("teste@lasanhaspec.com");
        user.setPassword("hash-qualquer");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        Optional<User> encontrado = userRepository.findByEmail("teste@lasanhaspec.com");

        assertTrue(encontrado.isPresent());
        assertEquals("teste@lasanhaspec.com", encontrado.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenEmailDoesNotExist() {
        Optional<User> encontrado = userRepository.findByEmail("nao-existe@lasanhaspec.com");

        assertTrue(encontrado.isEmpty());
    }

    @Test
    void shouldTreatEmailAsCaseSensitiveByDefault() {
        User user = new User();
        user.setEmail("Maiuscula@lasanhaspec.com");
        user.setPassword("hash-qualquer");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        // documenta o comportamento atual: busca exata nao acha email com caixa diferente.
        // se um dia isso virar bug real (usuario nao consegue logar por causa de maiuscula),
        // é aqui que o teste vai quebrar e avisar.
        Optional<User> comCaixaDiferente = userRepository.findByEmail("maiuscula@lasanhaspec.com");

        assertTrue(comCaixaDiferente.isEmpty());
    }
}