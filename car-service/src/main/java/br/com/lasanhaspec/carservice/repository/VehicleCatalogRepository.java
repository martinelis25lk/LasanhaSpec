package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.catalog.VehicleCatalog;
import org.springframework.data.jpa.repository.JpaRepository;


public interface VehicleCatalogRepository extends JpaRepository<VehicleCatalog, Long> {
}
