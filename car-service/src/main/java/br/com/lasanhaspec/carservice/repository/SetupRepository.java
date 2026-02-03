package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.models.SetupModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SetupRepository extends JpaRepository<SetupModel, Long> {
}
