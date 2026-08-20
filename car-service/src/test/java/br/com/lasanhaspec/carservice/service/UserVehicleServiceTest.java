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
import br.com.lasanhaspec.carservice.exception.BusinessException;


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
        User user = new User();
        user.setId(1L);
        user.setEmail("user@test.com");

        UserVehicle vehicle = new UserVehicle();
        vehicle.setId(1L);
        vehicle.setUserId(2L); // Dono diferente do usuário autenticado

        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(user));
        when(userVehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));

        assertThrows(BusinessException.class, () -> {
            userVehicleService.getVehicleByIdForAuthenticatedUser(1L, "user@test.com");
        });
    }
}