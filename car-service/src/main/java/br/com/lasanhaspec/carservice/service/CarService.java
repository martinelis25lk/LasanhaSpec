package br.com.lasanhaspec.carservice.service;


import br.com.lasanhaspec.carservice.domain.entity.Car;
import br.com.lasanhaspec.carservice.repository.CarRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {


    private final CarRepository carRepository;

    public CarService(CarRepository carRepository){
        this.carRepository = carRepository;
    }


    public Car save(Car car){
        return carRepository.save(car);
    }


    public List<Car> findAll(){
        return carRepository.findAll();
    }


    public Car findById(Long id){
        return carRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Car not found"));
    }


}
