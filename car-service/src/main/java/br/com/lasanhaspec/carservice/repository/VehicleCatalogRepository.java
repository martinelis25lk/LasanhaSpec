package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.models.VehicleCatalogModel;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VehicleCatalogRepository extends JpaRepository<VehicleCatalogModel, Long> {
}
