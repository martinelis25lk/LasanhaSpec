package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.entity.VehicleModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepository extends JpaRepository<VehicleModel, Long> {
}
