package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.UserRepository;
import br.com.lasanhaspec.carservice.repository.UserVehicleRepository;
import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import br.com.lasanhaspec.carservice.repository.VehicleImageRepository;
import br.com.lasanhaspec.carservice.infrastructure.storage.S3StorageService;
import br.com.lasanhaspec.carservice.domain.models.UserVehicle;
import br.com.lasanhaspec.carservice.domain.models.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserVehicleServiceTest {

    @Mock private UserVehicleRepository userVehicleRepository;
    @Mock private VehicleImageRepository vehicleImageRepository;
    @Mock private S3StorageService storageService;
    @Mock private VehicleCatalogRepository vehicleCatalogRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private UserVehicleService userVehicleService;

    private User owner;
    private UserVehicle vehicle;

    @BeforeEach
    void setUp() {
        owner = new User();
        owner.setEmail("dono@lasanhaspec.com");

        vehicle = new UserVehicle();
        vehicle.setId(1L);
        vehicle.setUser(owner);
    }

    @Test
    void shouldThrowWhenUserTriesToAccessAnotherUsersVehicle() {
        // Garante que um usuário não consegue acessar o veículo de outro
        // Essa é a regra de segurança mais crítica do sistema
        when(userVehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(userRepository.findByEmail("invasor@lasanhaspec.com"))
                .thenReturn(Optional.of(new User()));

        assertThrows(ResourceNotFoundException.class, () ->
                userVehicleService.uploadVehicleImage(1L, null, "invasor@lasanhaspec.com")
        );
    }
}