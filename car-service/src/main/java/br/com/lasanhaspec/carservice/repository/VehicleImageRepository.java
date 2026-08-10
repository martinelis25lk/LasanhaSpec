package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.models.VehicleImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface VehicleImageRepository extends JpaRepository<VehicleImage, Long> {

    List<VehicleImage> findByUserVehicleId(Long vehicleId);

    Optional<VehicleImage> findByIdAndUserVehicleId(Long imageId, Long vehicleId);

    Long countByUserVehicleId(Long vehicleId);

    Optional<VehicleImage> findByUserVehicleIdAndPrimaryImageTrue(Long vehicleId);

    boolean existsByUserVehicleId(Long vehicleId);

    @Modifying
    @Query("UPDATE VehicleImage vi SET vi.primaryImage = false WHERE vi.userVehicle.id = :vehicleId")
    void clearPrimaryByVehicle(@Param("vehicleId") Long vehicleId);
}