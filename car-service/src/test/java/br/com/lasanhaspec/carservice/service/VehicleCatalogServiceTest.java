package br.com.lasanhaspec.carservice.service;

import br.com.lasanhaspec.carservice.domain.enums.AspirationType;
import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import br.com.lasanhaspec.carservice.dto.CreateVehicleCatalogModelDTO;
import br.com.lasanhaspec.carservice.dto.VehicleCatalogDTO;
import br.com.lasanhaspec.carservice.exception.ResourceNotFoundException;
import br.com.lasanhaspec.carservice.repository.VehicleCatalogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleCatalogServiceTest {

    @Mock
    private VehicleCatalogRepository vehicleCatalogRepository;

    @InjectMocks
    private VehicleCatalogService vehicleCatalogService;

    private VehicleCatalogModel existingVehicle;

    @BeforeEach
    void setUp() {
        existingVehicle = new VehicleCatalogModel();
        existingVehicle.setId(1L);
        existingVehicle.setBrand("BMW");
        existingVehicle.setModel("E36 (325i/328i)");
        existingVehicle.setYear(1995);
        existingVehicle.setAspirationType(AspirationType.NATURALLY_ASPIRATED);
    }

    // ---------- save ----------

    @Test
    void shouldSaveVehicleAndReturnIt() {
        when(vehicleCatalogRepository.save(existingVehicle)).thenReturn(existingVehicle);

        VehicleCatalogModel result = vehicleCatalogService.save(existingVehicle);

        assertEquals(existingVehicle, result);
        verify(vehicleCatalogRepository).save(existingVehicle);
    }

    // ---------- findAll ----------

    @Test
    void shouldReturnAllVehicles() {
        when(vehicleCatalogRepository.findAll()).thenReturn(List.of(existingVehicle));

        List<VehicleCatalogModel> result = vehicleCatalogService.findAll();

        assertEquals(1, result.size());
        assertEquals("BMW", result.get(0).getBrand());
    }

    // ---------- findById ----------

    @Test
    void shouldReturnVehicleWhenIdExists() {
        when(vehicleCatalogRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));

        VehicleCatalogModel result = vehicleCatalogService.findById(1L);

        assertEquals("BMW", result.getBrand());
    }

    @Test
    void shouldThrowWhenIdDoesNotExistOnFindById() {
        when(vehicleCatalogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> vehicleCatalogService.findById(999L));
    }

    // ---------- updateVehicleCatalog ----------

    @Test
    void shouldUpdateVehicleFieldsAndReturnDTO() {
        CreateVehicleCatalogModelDTO dto = new CreateVehicleCatalogModelDTO();
        dto.setBrand("BMW");
        dto.setModel("E36 (325i/328i)");
        dto.setYear(1996);
        dto.setEngineCode("M50B25");
        dto.setFactoryHorsePower(192);
        dto.setFactoryTorque(245);
        dto.setFactoryWeight(1350);
        dto.setAspirationType("NATURALLY_ASPIRATED");
        dto.setFipeBrandCode("7");
        dto.setFipeModelCode("179");
        dto.setFipeYearCode("1995-1");

        when(vehicleCatalogRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));
        when(vehicleCatalogRepository.save(any(VehicleCatalogModel.class))).thenAnswer(inv -> inv.getArgument(0));

        VehicleCatalogDTO result = vehicleCatalogService.updateVehicleCatalog(1L, dto);

        assertEquals("M50B25", existingVehicle.getEngineCode());
        assertEquals(192, existingVehicle.getFactoryHorsepower());
        assertEquals(AspirationType.NATURALLY_ASPIRATED, existingVehicle.getAspirationType());
        assertEquals("M50B25", result.getEngineCode());
        verify(vehicleCatalogRepository).save(existingVehicle);
    }

    @Test
    void shouldThrowWhenIdDoesNotExistOnUpdate() {
        CreateVehicleCatalogModelDTO dto = new CreateVehicleCatalogModelDTO();
        dto.setAspirationType("NATURALLY_ASPIRATED");

        when(vehicleCatalogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleCatalogService.updateVehicleCatalog(999L, dto));

        verify(vehicleCatalogRepository, never()).save(any());
    }

    // ---------- deleteVehicleCatalogModel ----------

    @Test
    void shouldDeleteVehicleWhenIdExists() {
        when(vehicleCatalogRepository.findById(1L)).thenReturn(Optional.of(existingVehicle));

        vehicleCatalogService.deleteVehicleCatalogModel(1L);

        verify(vehicleCatalogRepository).delete(existingVehicle);
    }

    @Test
    void shouldThrowWhenIdDoesNotExistOnDelete() {
        when(vehicleCatalogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> vehicleCatalogService.deleteVehicleCatalogModel(999L));

        verify(vehicleCatalogRepository, never()).delete(any());
    }
}