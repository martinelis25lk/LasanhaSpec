package br.com.lasanhaspec.carservice.repository;

import br.com.lasanhaspec.carservice.domain.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepository extends JpaRepository<Car, Long> {
}
